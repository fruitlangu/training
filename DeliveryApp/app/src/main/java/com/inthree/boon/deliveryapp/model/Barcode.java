package com.inthree.boon.deliveryapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This is the class for bar code data to get the details from the aadhaar card
 */
public class Barcode {
    @SerializedName("PrintLetterBarcodeData")
    @Expose
    private PrintLetterBarcodeData printLetterBarcodeData;

    public PrintLetterBarcodeData getPrintLetterBarcodeData() {
        return printLetterBarcodeData;
    }

    public void setPrintLetterBarcodeData(PrintLetterBarcodeData printLetterBarcodeData) {
        this.printLetterBarcodeData = printLetterBarcodeData;
    }
}

