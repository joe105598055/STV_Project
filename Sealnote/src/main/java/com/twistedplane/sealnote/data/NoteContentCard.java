package com.twistedplane.sealnote.data;


import com.twistedplane.sealnote.R;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;


/**
 * Implementation of NoteContent for Note.Type = CARD.
 */
public class NoteContentCard extends NoteContent {
    public final static String TAG = "NoteContentCard";

    /**
     * Card Brands
     */
    private final static HashMap<String, Pattern> sCardBrands = new HashMap<String, Pattern>();
    static {
        sCardBrands.put("VISA", Pattern.compile("^4[0-9]{6,}$"));
        sCardBrands.put("MASTERCARD", Pattern.compile("^5[1-5][0-9]{5,}$"));
        sCardBrands.put("AMERICAN_EXPRESS", Pattern.compile("^3[47][0-9]{5,}$"));
        sCardBrands.put("DISCOVER", Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{3,}$"));
        sCardBrands.put("DINERS_CLUB", Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{4,}$"));
        sCardBrands.put("JCB", Pattern.compile("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$"));
    }

    private final static HashMap<String, Integer> sCardDrawables = new HashMap<String, Integer>();
    static {
        sCardDrawables.put("VISA", R.drawable.card_brand_visa);
        sCardDrawables.put("MASTERCARD", R.drawable.card_brand_mastercard);
        sCardDrawables.put("AMERICAN_EXPRESS", R.drawable.card_brand_americanexpress);
        sCardDrawables.put("DISCOVER", R.drawable.card_brand_discover);
        sCardDrawables.put("DINERS_CLUB", R.drawable.card_brand_diners);
        sCardDrawables.put("JCB", R.drawable.card_brand_jcb);
        sCardDrawables.put("UNKNOWN", R.drawable.card_brand_unknown);
    }

    private String mName;
    private String mNumber;
    private String mValidFrom;
    private String mValidUpto;
    private String mCvv;
    private String mAdditionalNote;

    protected NoteContentCard(String content) {
        super(content);
        mName = mNumber = mValidFrom = mValidUpto = mCvv = mAdditionalNote = "";
    }

    public NoteContentCard(String name, String number, String validFrom,
                           String validUpto, String cvv, String additionalNote) {
        super();
        mName = name;
        mNumber = number;
        mValidFrom = validFrom;
        mValidUpto = validUpto;
        mCvv = cvv;
        mAdditionalNote = additionalNote;
    }

    /**
     * Masks card number with '*' showing only last group of digits.
     */
    private String maskCardNumber(String cardNumber) {
        int lastGroupStart = cardNumber.lastIndexOf('-');
        if (lastGroupStart < 0) {
            return cardNumber;
        }

        String lastGroup = cardNumber.substring(lastGroupStart, cardNumber.length());
        String prefixGroup = cardNumber.substring(0, lastGroupStart).replaceAll("\\d", "*");
        return prefixGroup + "<b>" + lastGroup + "</b>";
    }

    @Override
    public String getCardString() {
        if (!mUpdated) {
            update();
        }
        if (mNumber.equals("") && mName.equals("")) {
            return "Card details";
        } else if (mNumber.equals("")) {
            return "CARD/" + mName;
        }

        String result = mName;
        result += result.equals("") ?"" :"<br/>";

        String brand = getBrand(mNumber);
        result += "<b>" + (brand.equals("UNKNOWN") ?"CARD" :brand) + "</b>/" + maskCardNumber(mNumber);
        return result;
    }

    @Override
    public void update() {
        Scanner scanner = new Scanner(mContent);

        try {
            mName = scanner.nextLine();
            mNumber = scanner.nextLine();
            mValidFrom = scanner.nextLine();
            mValidUpto = scanner.nextLine();
            mCvv = scanner.nextLine();
            mAdditionalNote = scanner.nextLine();
            while (scanner.hasNextLine()) {
                mAdditionalNote += "\n";
                mAdditionalNote += scanner.nextLine();
            }
        } catch (NoSuchElementException e) {
            // Log.e(TAG, "Insufficient data in scanner stream. Done reading.");
        }

        mUpdated = true;
    }

    @Override
    public String toString() {
        final String format = "%s\n%s\n%s\n%s\n%s\n%s";
        return String.format(format,
                mName, mNumber, mValidFrom, mValidUpto, mCvv, mAdditionalNote
        );
    }

    /**
     * Detect card brand from given card number
     * @return  Card brand code string, or UNKNOWN if nothing found
     */
    public static String getBrand(String cardNumber) {
        String cleanedCardNumber = cardNumber.replace(" ", "").replace("-", "");

        for (String key : sCardBrands.keySet()) {
            if (sCardBrands.get(key).matcher(cleanedCardNumber).matches()) {
                return key;
            }
        }
        return "UNKNOWN";
    }

    /**
     * Get card brand logo drawable
     * @param brandName Brand name code of card
     * @return          Resource id of drawble
     */
    public static int getBrandDrawableId(String brandName) {
        return sCardDrawables.get(brandName);
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        if (!mUpdated) {
            update();
        }
        return mName;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getNumber() {
        if (!mUpdated) {
            update();
        }
        return mNumber;
    }

    public void setValidFrom(String validFrom) {
        mValidFrom = validFrom;
    }

    public String getValidFrom() {
        if (!mUpdated) {
            update();
        }
        return mValidFrom;
    }

    public void setValidUpto(String validUpto) {
        mValidUpto = validUpto;
    }

    public String getValidUpto() {
        if (!mUpdated) {
            update();
        }
        return mValidUpto;
    }

    public void setCvv(String cvv) {
        mCvv = cvv;
    }

    public String getCvv() {
        if (!mUpdated) {
            update();
        }
        return mCvv;
    }

    public void setAdditionalNote(String additionalNote) {
        mAdditionalNote = additionalNote;
    }

    public String getAdditionalNote() {
        if (!mUpdated) {
            update();
        }
        return mAdditionalNote;
    }
}