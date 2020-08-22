package com.pawel.wojtanka;

import java.time.LocalDate;

public class DateInterval {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateInterval(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

}