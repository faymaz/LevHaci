package com.perfboarddesigner.model;

public enum DIPPackage {
    DIP4("DIP4", 4, 2, "Optocoupler, Basic Logic"),
    DIP6("DIP6", 6, 3, "Op-amp, Timer"),
    DIP8("DIP8", 8, 4, "555 Timer, Op-amp, EEPROM"),
    DIP10("DIP10", 10, 5, "DAC, ADC"),
    DIP12("DIP12", 12, 6, "Op-amp Array"),
    DIP14("DIP14", 14, 7, "TTL Logic, Counters"),
    DIP16("DIP16", 16, 8, "Microcontroller, SRAM"),
    DIP18("DIP18", 18, 9, "PIC Microcontroller"),
    DIP20("DIP20", 20, 10, "Microcontroller, Interface"),
    DIP22("DIP22", 22, 11, "Microcontroller"),
    DIP24("DIP24", 24, 12, "EPROM, SRAM"),
    DIP28("DIP28", 28, 14, "ATmega328, EPROM"),
    DIP40("DIP40", 40, 20, "8-bit CPU, Large MCU");

    private final String displayName;
    private final int pinCount;
    private final int pinsPerSide;
    private final String commonChips;

    DIPPackage(String displayName, int pinCount, int pinsPerSide, String commonChips) {
        this.displayName = displayName;
        this.pinCount = pinCount;
        this.pinsPerSide = pinsPerSide;
        this.commonChips = commonChips;
    }

    public String getDisplayName() { return displayName; }
    public int getPinCount() { return pinCount; }
    public int getPinsPerSide() { return pinsPerSide; }
    public String getCommonChips() { return commonChips; }
    
    public String getFullDisplayName() {
        return displayName + " (" + commonChips + ")";
    }
}
