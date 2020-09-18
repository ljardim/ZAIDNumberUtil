package com.github.ljardim.utils;

import com.github.ljardim.utils.zaid.ZAIdData;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.regex.Pattern;

public class ZAIDNumberUtil {
    /**
     * The start sequence for indicating a MALE id number.
     */
    private static final int GENDER_START_SEQUENCE = 5000;

    /**
     * The default maximum age we deem a person to be able to achieve. Can be
     * overriden by providing this value on the parse call.
     */
    private static final int MAX_PERSON_AGE = 100;

    /**
     * Pattern for validating that an input is 13 digits.
     */
    private static final Pattern PATTERN_13_DIGITS = Pattern.compile("[0-9]{13}");

    /**
     * Last integer digit that is singular.
     */
    private static final int LAST_SINGLE_DIGIT = 9;

    private static final int INDEX_DOB_YEAR_START = 0;
    private static final int INDEX_DOB_YEAR_END = 2;
    private static final int INDEX_DOB_MONTH_START = 2;
    private static final int INDEX_DOB_MONTH_END = 4;
    private static final int INDEX_DOB_DAY_START = 4;
    private static final int INDEX_DOB_DAY_END = 6;
    private static final int INDEX_GENDER_START = 6;
    private static final int INDEX_GENDER_END = 10;
    private static final int INDEX_CITIZENSHIP_START = 10;
    private static final int INDEX_CITIZENSHIP_END = 11;
    private static final int INDEX_CHECK_DIGIT_START = 12;
    private static final int INDEX_CHECK_DIGIT_END = 13;

    private ZAIDNumberUtil() {
    }

    /**
     * Parses the id number by first performing validation and then extracting data
     * from id. Uses the default {@link ZAIDNumberUtil#MAX_PERSON_AGE}
     *
     * @param inputId The input id number that will be validated and data from.
     *
     * @return An object that encapsulates the validation result of the ID Number,
     *         and if valid, the data that was extracted from the id number.
     */
    public static ZAIdData parse(final String inputId) {
        return parse(inputId, MAX_PERSON_AGE);
    }

    /**
     * <p>
     * Parses the id number by first performing validation and then extracting data
     * from id.
     * </p>
     *
     * <p>
     * Input will be trimmed after non-null validation.
     * </p>
     *
     * @param inputId      The input id number that will be validated and data
     *                     parsed from.
     * @param maxPersonAge The maximum age a person is deemed to be a to.
     *
     * @return An object that encapsulates the validation result of the ID Number,
     *         and if valid, the data that was extracted from the id number.
     */
    public static ZAIdData parse(final String inputId, final int maxPersonAge) {
        final ZAIdData.InvalidReason invalidReason = performValidation(inputId, maxPersonAge);
        if (invalidReason != null) {
            return ZAIdData.builder().valid(false).invalidReason(invalidReason).build();
        }

        final String trimmedInputId = inputId.trim();

        return ZAIdData.builder().idNumber(trimmedInputId).valid(true)
                .dateOfBirth(LocalDate.of(getYear(trimmedInputId, maxPersonAge),
                        Month.of(Integer.parseInt(getMonthDigits(trimmedInputId))),
                        Integer.parseInt(getDayDigits(trimmedInputId))))
                .gender(Integer.parseInt(getGenderDigits(trimmedInputId)) >= GENDER_START_SEQUENCE
                        ? ZAIdData.Gender.MALE
                        : ZAIdData.Gender.FEMALE)
                .citizenshipStatus(
                        Integer.parseInt(getCitizenshipDigits(trimmedInputId)) == 0 ? ZAIdData.CitizenshipStatus.CITIZEN
                                : ZAIdData.CitizenshipStatus.PERMANENT_RESIDENT)
                .build();
    }

    /**
     * <p>
     * Performs validation on the provided RSA ID Number. Uses the default
     * {@link ZAIDNumberUtil#MAX_PERSON_AGE}.
     * </p>
     *
     * @see #performValidation(String, int)
     */
    public static ZAIdData.InvalidReason performValidation(final String inputId) {
        return performValidation(inputId, MAX_PERSON_AGE);
    }

    /**
     * <p>
     * Performs validation on the provided RSA ID Number. Uses the given
     * maxPersonAge for date of birth field validations.
     * </p>
     *
     * <p>
     * Input ID will be trimmed after non-null validation
     * </p>
     *
     * <p>
     * Validations that will be performed and the relevant response REASON CODE:
     * </p>
     *
     * <pre>
     * ZAIDNumberUtil.performValidation(null)               = {@link ZAIdData.InvalidReason#INPUT_BLANK}
     * ZAIDNumberUtil.performValidation("")                 = {@link ZAIdData.InvalidReason#INPUT_BLANK}
     * ZAIDNumberUtil.performValidation(" ")                = {@link ZAIdData.InvalidReason#INPUT_BLANK}
     * ZAIDNumberUtil.performValidation("abcdefghijklm")    = {@link ZAIdData.InvalidReason#NOT_13_DIGITS}
     * ZAIDNumberUtil.performValidation("123")              = {@link ZAIdData.InvalidReason#NOT_13_DIGITS}
     * ZAIDNumberUtil.performValidation("1234567890123")    = {@link ZAIdData.InvalidReason#INVALID_MONTH_DIGITS}
     * ZAIDNumberUtil.performValidation("1212567890123")    = {@link ZAIdData.InvalidReason#INVALID_DAYS_DIGITS}
     * ZAIDNumberUtil.performValidation("1212107890123")    = {@link ZAIdData.InvalidReason#CHECK_DIGIT_VERIFICATION_FAILED}
     * ZAIDNumberUtil.performValidation({valid RSA ID})     = {@code null}
     * </pre>
     *
     * @param inputId      The input to validate.
     * @param maxPersonAge The maximum age a person is deemed to be a to.
     *
     * @return If a validation issue occured, then the validation reason code will
     *         be returned, if not, then null will be returned.
     */
    public static ZAIdData.InvalidReason performValidation(final String inputId, final int maxPersonAge) {
        // Null and blank check
        if (inputId == null || inputId.isBlank()) {
            return ZAIdData.InvalidReason.INPUT_BLANK;
        }

        final String trimmedInputId = inputId.trim();

        // Expecting 13 digit string
        if (!PATTERN_13_DIGITS.matcher(trimmedInputId).matches()) {
            return ZAIdData.InvalidReason.NOT_13_DIGITS;
        }

        // Basic month validation
        final int monthDigit = Integer.parseInt(getMonthDigits(trimmedInputId));
        if (monthDigit > Month.values().length) {
            return ZAIdData.InvalidReason.INVALID_MONTH_DIGITS;
        }

        final int year = getYear(trimmedInputId, maxPersonAge);

        // Validate days of month
        final int daysInMonth = YearMonth.of(year, monthDigit).lengthOfMonth();
        if (daysInMonth != Integer.parseInt(getDayDigits(trimmedInputId))) {
            return ZAIdData.InvalidReason.INVALID_DAYS_DIGITS;
        }

        // Validate against Luhn algorithm
        final int checkbit = Integer.parseInt(getCheckDigits(trimmedInputId));
        if (computeLuhnCheckDigit(trimmedInputId.substring(0, INDEX_CHECK_DIGIT_START - 1)) != checkbit) {
            return ZAIdData.InvalidReason.CHECK_DIGIT_VERIFICATION_FAILED;
        }

        return null;
    }

    private static int getYear(final String input, final int maxPersonAge) {
        final Year baseYear = Year.of(Year.now().getValue() - maxPersonAge);
        final DateTimeFormatter yearFormatter = new DateTimeFormatterBuilder()
                .appendValueReduced(ChronoField.YEAR, 2, 2, baseYear.getValue()).toFormatter();
        return Year.parse(getYearDigits(input), yearFormatter).getValue();
    }

    /**
     * <p>
     * Extract the year digits from the input.
     * </p>
     *
     * <p>
     * It is expected that the ID basic validation has passed before calling this
     * method, so it is not null safe.
     * </p>
     *
     * @param input The input RSA ID Number.
     *
     * @return The 2 digit year digits from the RSA ID Number.
     */
    private static String getYearDigits(final String input) {
        return input.substring(INDEX_DOB_YEAR_START, INDEX_DOB_YEAR_END);
    }

    /**
     * <p>
     * Extract the month digits from the input.
     * </p>
     *
     * <p>
     * It is expected that the ID basic validation has passed before calling this
     * method, so it is not null safe.
     * </p>
     *
     * @param input The input RSA ID Number.
     *
     * @return The 2 digit month digits from the RSA ID Number.
     */
    private static String getMonthDigits(final String input) {
        return input.substring(INDEX_DOB_MONTH_START, INDEX_DOB_MONTH_END);
    }

    /**
     * <p>
     * Extract the days digits from the input.
     * </p>
     *
     * <p>
     * It is expected that the ID basic validation has passed before calling this
     * method, so it is not null safe.
     * </p>
     *
     * @param input The input RSA ID Number.
     *
     * @return The 2 digit day digits from the RSA ID Number.
     */
    private static String getDayDigits(final String input) {
        return input.substring(INDEX_DOB_DAY_START, INDEX_DOB_DAY_END);
    }

    /**
     * <p>
     * Extract the gender sequence digits from the input.
     * </p>
     *
     * <p>
     * It is expected that the ID basic validation has passed before calling this
     * method, so it is not null safe.
     * </p>
     *
     * @param input The input RSA ID Number.
     *
     * @return The 4 digit gender sequence digits from the RSA ID Number.
     */
    private static String getGenderDigits(final String input) {
        return input.substring(INDEX_GENDER_START, INDEX_GENDER_END);
    }

    /**
     * <p>
     * Extract the citizenship digit from the input.
     * </p>
     *
     * <p>
     * It is expected that the ID basic validation has passed before calling this
     * method, so it is not null safe.
     * </p>
     *
     * @param input The input RSA ID Number.
     *
     * @return The 1 digit citizenship indicator digit from the RSA ID Number.
     */
    private static String getCitizenshipDigits(final String input) {
        return input.substring(INDEX_CITIZENSHIP_START, INDEX_CITIZENSHIP_END);
    }

    /**
     * <p>
     * Extract the check digit from the input.
     * </p>
     *
     * <p>
     * It is expected that the ID basic validation has passed before calling this
     * method, so it is not null safe.
     * </p>
     *
     * @param input The input RSA ID Number.
     *
     * @return The 1 digit check digit from the RSA ID Number.
     */
    private static String getCheckDigits(final String input) {
        return input.substring(INDEX_CHECK_DIGIT_START, INDEX_CHECK_DIGIT_END);
    }

    /**
     * Uses the Luhn Algorithm to calculate the checkdigit for the input. The input
     * should be the id number without the last digit.
     *
     * @param input The South African ID Number excluding the last digit.
     *
     * @return The Luhn Check digit which can be compared to the last digit of the
     *         full id for verification.
     * @see <a href=
     *      "https://en.wikipedia.org/wiki/Luhn_algorithm">https://en.wikipedia.org/wiki/Luhn_algorithm</a>
     */
    private static int computeLuhnCheckDigit(final String input) {
        final int[] ints = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            ints[i] = Integer.parseInt(input.substring(i, i + 1));
        }
        for (int i = ints.length - 2; i >= 0; i = i - 2) {
            int j = ints[i];
            j = j * 2;
            if (j > LAST_SINGLE_DIGIT) {
                j = j % (LAST_SINGLE_DIGIT + 1) + 1;
            }
            ints[i] = j;
        }
        int sum = 0;
        for (final int anInt : ints) {
            sum += anInt;
        }
        if (sum % (LAST_SINGLE_DIGIT + 1) == 0) {
            return 0;
        } else {
            return (LAST_SINGLE_DIGIT + 1) - (sum % (LAST_SINGLE_DIGIT + 1));
        }
    }
}
