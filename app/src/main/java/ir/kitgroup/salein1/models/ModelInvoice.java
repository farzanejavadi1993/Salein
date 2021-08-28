package ir.kitgroup.salein1.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ir.kitgroup.salein1.DataBase.Invoice;
import ir.kitgroup.salein1.DataBase.InvoiceDetail;



public class ModelInvoice {

    @SerializedName("Invoice")
    @Expose
    private List<Invoice> invoice ;
    @SerializedName("InvoiceDetail")
    @Expose
    private List<InvoiceDetail> invoiceDetail ;

    public List<Invoice> getInvoice() {
        return invoice;
    }

    public void setInvoice(List<Invoice> invoice) {
        this.invoice = invoice;
    }

    public List<InvoiceDetail> getInvoiceDetail() {
        return invoiceDetail;
    }

    public void setInvoiceDetail(List<InvoiceDetail> invoiceDetail) {
        this.invoiceDetail = invoiceDetail;
    }
}
