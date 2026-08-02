package com.invoicesync.modules.invoice.extraction;

public class InvoiceExtractionPromptBuilder {

  private InvoiceExtractionPromptBuilder() {
  }

  //
  private static final String PROMPT_TEMPLATE =
      """
          You are an invoice data extraction tool.
          Invoice documents may come from different suppliers and have different layouts.
          Do not rely on text position or formatting.
          Extract information based on semantic meaning and context.
          Your output must be exactly one valid JSON object and nothing else.
          Do not include explanations, comments, Markdown, or any text before or after the JSON.
          """;

  private static String headOnlyPrompt() {
    return PROMPT_TEMPLATE + """
        
        Return exactly the following JSON structure:
        
        {
          "supplier_registration_number": "supplier registration number IČO",
          "invoice_number": "invoice number or null",
          "issue_date": "YYYY-MM-DD or null",
          "due_date": "YYYY-MM-DD or null",
          "currency": "EUR/CZK/...",
          "invoice_total_price_without_vat": number or null
          "invoice_total_price_with_vat": number or null
        }
        
        Do NOT return the list of invoice items or describe them.
        Only extract the summary information shown above.
        """;
  }

  public static String withItemsPrompt() {
    return PROMPT_TEMPLATE + """
        
        Return exactly the following JSON structure:
        
         {
           "supplier_registration_number": "supplier registration number",
           "invoice_number": "invoice number or null",
           "issue_date": "YYYY-MM-DD or null",
           "due_date": "YYYY-MM-DD or null",
           "currency": "EUR/CZK/...",
           "invoice_total_price_without_vat": number or null
           "invoice_total_price_with_vat": number or null
           "items": [
             {
               "name": "item name",
               "quantity": number,
               "unit": "pcs/kg/l/...",
               "vat": "number"
               "unit_price": number or null,
               "total_price": number or null
             }
           ]
         }
        """;
  }

  public static String buildPrompt(ExtractionMode mode) {
    return switch (mode) {
      case HEADERS_ONLY -> headOnlyPrompt();
      case WITH_ITEMS -> withItemsPrompt();
    };
  }

  public static int recommendedMaxTokens(ExtractionMode mode) {
    return switch (mode) {
      case HEADERS_ONLY -> 400;
      case WITH_ITEMS -> 4000;
    };
  }

}
