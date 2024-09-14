package com.example.omega.service;

import com.example.omega.repository.TransactionRepository;
import com.example.omega.service.util.TransactionServiceUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.example.omega.config.InstantSerializer.formatter;

@Service
@AllArgsConstructor
public class TransactionReportService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final TransactionServiceUtil transactionServiceUtil;

    /**
     * Generates a transaction report PDF for a specified user and date range.
     * This method orchestrates the creation of a transaction report document. It begins by fetching the user's name tag
     * based on the provided user ID. A new PDF document is then initialized, and various sections are added to this document,
     * including a report title, the user's name tag, the date range of the report, and a table of transactions within the specified
     * date range. After all sections have been added, the document is closed, and the content is written to a byte array output stream.
     * The byte array representing the PDF document is then returned.
     *
     * @param userId    The ID of the user for whom the transaction report is being generated.
     * @param startDate The start date of the period for which the transaction report is generated.
     * @param endDate   The end date of the period for which the transaction report is generated.
     * @return A byte array containing the generated PDF document.
     * @throws DocumentException If an error occurs during the document creation process.
     */
    public byte[] generateTransactionReport(Long userId, LocalDate startDate, LocalDate endDate) throws DocumentException {
        var userNameTag = userService.getUserById(userId).getNameTag();
        var document = new Document();
        var outputStream = new ByteArrayOutputStream();
        var writer = PdfWriter.getInstance(document, outputStream);

        document.open();
        addReportTitle(document);
        addUserNameTag(document, userNameTag);
        addDateRange(document, startDate, endDate);
        addTransactionsTable(document, userId, startDate, endDate, writer);
        document.close();

        return outputStream.toByteArray();
    }

    /**
     * Adds a title to the PDF document.
     * This method creates a title for the document using a specified font and alignment. The title is set to "Transaction Report".
     * After adding the title, a new line is inserted for spacing, ensuring that subsequent elements are visually separated.
     *
     * @param document The PDF document to which the title is added.
     * @throws DocumentException If there is an error during the document modification process.
     */
    private void addReportTitle(Document document) throws DocumentException {
        var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        var title = new Paragraph("Transaction Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));
    }

    /**
     * Adds the user's name  tag paragraph to a PDF document.
     * This method creates a paragraph containing the user's name tag, aligns it to the center of the document,
     * and then adds it to the document. A new line is added after the name tag for spacing.
     *
     * @param document    The PDF document to which the user's name ag paragraph is added.
     * @param userNameTag The user's name tag to be displayed in the document.
     * @throws DocumentException If there is an error during the document modification process.
     */
    private void addUserNameTag(Document document, String userNameTag) throws DocumentException {
        var nameTag = new Paragraph(new Paragraph("User: " + userNameTag));
        nameTag.setAlignment(Element.ALIGN_CENTER);
        document.add(nameTag);
        document.add(new Paragraph("\n"));
    }

    /**
     * Adds a date range paragraph to a PDF document.
     * This method creates a paragraph indicating the date range from the start date to the end date,
     * aligns it to the center of the document, and then adds it to the document. A new line is added
     * after the date range for spacing.
     *
     * @param document  The PDF document to which the date range paragraph is added.
     * @param startDate The start date of the date range.
     * @param endDate   The end date of the date range.
     * @throws DocumentException If there is an error during the document modification process.
     */
    private void addDateRange(Document document, LocalDate startDate, LocalDate endDate) throws DocumentException {
        var dateRange = new Paragraph("Date Range: " + startDate + " - " + endDate);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        document.add(new Paragraph("\n"));
    }


    /**
     * Adds a transactions table and a summary of total calculations to the PDF document.
     * This method performs several steps to include detailed transaction information and a summary of total funds
     * added and spent within a specified date range for a given user. Initially, it creates a table with predefined
     * headers for transaction details. It then populates this table with transactions that occurred between the
     * specified start and end dates for the user. After adding the populated table to the document, it calculates
     * the total funds added and spent in the given period and adds a summary paragraph to the document. If the
     * current page does not have enough space for the summary, a new page is created.
     *
     * @param document  The PDF document to which the transactions table and summary paragraph are added.
     * @param userId    The ID of the user for whom the transactions report is being generated.
     * @param startDate The start date of the period for which transactions are summarized.
     * @param endDate   The end date of the period for which transactions are summarized.
     * @param writer    The PdfWriter instance used for writing to the document.
     * @throws DocumentException If there is an error during the document modification process.
     */
    private void addTransactionsTable(Document document, Long userId, LocalDate startDate, LocalDate endDate, PdfWriter writer) throws DocumentException {
        var table = createTransactionsTable();
        populateTransactionsTable(table, userId, startDate, endDate);
        document.add(table);
        addTotalCalculations(document, userId, startDate, endDate, writer);
    }

    /**
     * Creates a {@link PdfPTable} for displaying transaction details.
     * This method initializes a table with seven columns to display various attributes of transactions,
     * such as date, transaction type, description, amount, currency, sender, and recipient. It sets the
     * width of the table to span the entire page width and adjusts spacing before and after the table.
     * Column headers are added to the table to identify each piece of transaction information.
     *
     * @return A {@link PdfPTable} object ready to be populated with transaction data.
     */
    private PdfPTable createTransactionsTable() {
        var table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        addTableHeaders(table);
        return table;
    }

    /**
     * Adds column headers to a {@link PdfPTable}.
     * This method initializes each column header for the transactions table, setting the background color,
     * alignment, and text for each header cell. The headers include information such as the date of the transaction,
     * transaction type, description, amount, currency, sender, and recipient.
     *
     * @param table The {@link PdfPTable} to which the headers are added.
     */
    private void addTableHeaders(PdfPTable table) {
        var headers = new String[]{"Date", "Transaction Type", "Description", "Amount", "Currency", "Sender", "Recipient"};
        for (var header : headers) {
            var cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    /**
     * Populates a {@link PdfPTable} with transaction data for a specified user and date range.
     * This method retrieves transactions for the given user that occurred between the start and end dates,
     * formats each transaction's details, and adds them as rows to the provided table. Each row includes
     * the transaction's date, type, description, amount, currency, sender, and recipient.
     *
     * @param table     The {@link PdfPTable} to be populated with transaction data.
     * @param userId    The ID of the user whose transactions are to be displayed.
     * @param startDate The start date of the range within which transactions are considered.
     * @param endDate   The end date of the range within which transactions are considered.
     */
    private void populateTransactionsTable(PdfPTable table, Long userId, LocalDate startDate, LocalDate endDate) {
        var startDateTime = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        var endDateTime = endDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();
        var transactions = transactionRepository.findByUserIdAndCreatedDateBetween(userId, startDateTime, endDateTime);
        for (var transaction : transactions) {
            var formattedDate = formatDate(transaction.getCreatedDate());
            table.addCell(formattedDate);
            table.addCell(transaction.getTransactionType().toString());
            table.addCell(transaction.getDescription());
            table.addCell(String.valueOf(transaction.getAmount()));
            table.addCell(transaction.getCurrency().toString());
            table.addCell(transaction.getSender().getNameTag());
            table.addCell(transaction.getRecipient().getNameTag());
        }
    }

    /**
     * Formats an {@link Instant} to a {@link String} representation based on a predefined formatter.
     * This method converts the provided instant to a {@link LocalDateTime} using the system's default time zone,
     * then formats it according to the specified formatter.
     *
     * @param instant The instant to be formatted.
     * @return A string representation of the instant, formatted according to the predefined formatter.
     */
    private String formatDate(Instant instant) {
        var localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }

    /**
     * Adds a summary paragraph of total calculations to the PDF document.
     * This method calculates the total funds added and spent within a specified date range for a given user,
     * and then adds a summary paragraph to the document. If the current page does not have enough space,
     * a new page is created to accommodate the summary. The summary includes the total added funds and total spent,
     * formatted with a specified font.
     *
     * @param document  The PDF document to which the summary paragraph is added.
     * @param userId    The ID of the user for whom the report is being generated.
     * @param startDate The start date of the period for which transactions are summarized.
     * @param endDate   The end date of the period for which transactions are summarized.
     * @param writer    The PdfWriter instance used for writing to the document.
     * @throws DocumentException If there is an error during the document modification process.
     */
    private void addTotalCalculations(Document document, Long userId, LocalDate startDate, LocalDate endDate, PdfWriter writer) throws DocumentException {
        var startDateTime = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        var endDateTime = endDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant();
        var transactions = transactionRepository.findByUserIdAndCreatedDateBetween(userId, startDateTime, endDateTime);
        var calculations = transactionServiceUtil.calculateTotalAddedFundsAndTotalSpentInTimeRange(transactions, userId);
        var totalAddedFunds = calculations.getLeft();
        var totalSpent = calculations.getRight();

        var calculationsFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        var calculationsParagraph = createCalculationsParagraph(totalAddedFunds, totalSpent, calculationsFont);

        var pdfContentByte = writer.getDirectContent();
        var pageSize = document.getPageSize();
        var yPosition = pdfContentByte.getPdfDocument().getVerticalPosition(false);
        var calculationsParagraphHeight = calculationsParagraph.getLeading() * 2; // Multiply by 2 for two lines

        if (yPosition - calculationsParagraphHeight < pageSize.getBottom()) {
            document.newPage();
        }

        document.add(calculationsParagraph);
    }

    /**
     * Creates a paragraph for the PDF document that summarizes the total added funds and total spent.
     * This method constructs a paragraph detailing the total funds added to and spent from the user's account,
     * formatted with the specified font. The currency symbol is hardcoded as "$", which is a placeholder
     * and should ideally be replaced with the currency from the transaction data.
     *
     * @param totalAddedFunds  The total amount of funds added to the user's account.
     * @param totalSpent       The total amount of funds spent from the user's account.
     * @param calculationsFont The font to be used for the text in the paragraph.
     * @return A {@link Paragraph} object containing the formatted summary of transactions.
     */
    private Paragraph createCalculationsParagraph(BigDecimal totalAddedFunds, BigDecimal totalSpent, Font calculationsFont) {
        var calculationsParagraph = new Paragraph();
        calculationsParagraph.setAlignment(Element.ALIGN_LEFT);
        calculationsParagraph.setSpacingBefore(20f);
        //TODO: We should take the currency from the transaction itself
        calculationsParagraph.add(new Chunk("Total Added Funds: $", calculationsFont));
        calculationsParagraph.add(new Chunk(String.valueOf(totalAddedFunds)));
        calculationsParagraph.add(new Chunk("\nTotal Spent: $", calculationsFont));
        calculationsParagraph.add(new Chunk(String.valueOf(totalSpent)));
        return calculationsParagraph;
    }
}
