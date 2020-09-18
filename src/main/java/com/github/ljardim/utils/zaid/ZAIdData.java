package com.github.ljardim.utils.zaid;

import java.time.LocalDate;

public class ZAIdData {
    private final boolean valid;
    private final String idNumber;
    private final LocalDate dateOfBirth;
    private final Gender gender;
    private final CitizenshipStatus citizenshipStatus;

    public ZAIdData(final boolean valid, final String idNumber, final LocalDate dateOfBirth, final Gender gender,
            final CitizenshipStatus citizenshipStatus) {
        this.valid = valid;
        this.idNumber = idNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.citizenshipStatus = citizenshipStatus;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isValid() {
        return valid;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public CitizenshipStatus getCitizenshipStatus() {
        return citizenshipStatus;
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum CitizenshipStatus {
        CITIZEN, PERMANENT_RESIDENT
    }

    public enum InvalidReason {
        /**
         * The input ID Number was not provided or blank.
         */
        INPUT_BLANK,

        /**
         * The input ID Number provided is not a 13 digit string.
         */
        NOT_13_DIGITS,

        /**
         * The input ID Number provided failed the Luhn Check digit verification.
         */
        CHECK_DIGIT_VERIFICATION_FAILED,

        /**
         * The input ID Number's month digits are more than 12, which is not allowed as
         * there are only 12 months in a year.
         */
        INVALID_MONTH_DIGITS,

        /**
         * The input ID Number's days digits are more than the month can have in the
         * given year.
         */
        INVALID_DAYS_DIGITS
    }

    public static class Builder {
        private boolean valid;
        private InvalidReason invalidReason;
        private String idNumber;
        private LocalDate dateOfBirth;
        private Gender gender;
        private CitizenshipStatus citizenshipStatus;

        public Builder valid(final boolean input) {
            valid = input;
            return this;
        }

        public Builder invalidReason(final InvalidReason input) {
            invalidReason = input;
            return this;
        }

        public Builder idNumber(final String input) {
            idNumber = input;
            return this;
        }

        public Builder dateOfBirth(final LocalDate input) {
            dateOfBirth = input;
            return this;
        }

        public Builder gender(final Gender input) {
            gender = input;
            return this;
        }

        public Builder citizenshipStatus(final CitizenshipStatus input) {
            citizenshipStatus = input;
            return this;
        }

        public ZAIdData build() {
            return new ZAIdData(valid, idNumber, dateOfBirth, gender, citizenshipStatus);
        }
    }
}
