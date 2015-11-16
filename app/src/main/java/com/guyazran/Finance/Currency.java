package com.guyazran.Finance;

/**
 *  Created by guyazran on 11/4/15
 *  Currency is an enumeration that represents global currencies
 */

public enum Currency{
    USD, EUR, ILS;

    @Override
    public String toString() {
        switch (this){
            case USD:
                return "$";
            case EUR:
                return "€";
            case ILS:
                return "₪";
            default:
                return "";
        }
    }
}
