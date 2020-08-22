package com.pawel.wojtanka;

import java.time.LocalDate;

class DateInterval {
    private final LocalDate startDate;
    private final LocalDate endDate;

    DateInterval(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    LocalDate getStartDate() {
        return startDate;
    }

    LocalDate getEndDate() {
        return endDate;
    }

}