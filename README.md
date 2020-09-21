# South African ID Number utilities

## Structure of a RSA Id number

A South African ID Number is a 13 digit number defined by the following format:

`YYMMDDSSSSCAZ`

| PART    | DESCRIPTION                                                                                                                     |
|---------|---------------------------------------------------------------------------------------------------------------------------------|
| YYMMDD  | Indicates the date of birth of the person.                                                                                      |
| SSSS    | Sequence used to define the person's gender. Only the first digit is relevant. Females have a number 0 to 4, Males have 5 to 9. |
| C       | 0 if the person is a citizen, 1 if they are a permanent resident.                                                               |
| A       | Was used until the late 1980s to indicate the person's race. Was removed and old ID's have been reissued.                       |
| Z       | Checksum digit. Used to check that the sequence is valid using the Luhn algorithm.                                              |

## Basic steps of applying the Luhn algorithm

The last digit of a South African ID number is calculated using the Luhn algorithm, which allows for basic error detection.

For more information, see the [wikipedia](https://en.wikipedia.org/wiki/Luhn_algorithm) entry.

To check whether an ID number is valid, the Luhn algorithm may be applied as follows:

1. Working from the rightmost digit of the number, double every second digit.
2. Add the digits of this result together.
3. Sum together the resultant digits, with the remaining (odd) digits of the ID number.
4. If this sum is divisible by 10 (without remainder), the ID number is valid.

## Validations performed in order

| REASON CODE                     | DESCRIPTION                                                                                                        |
|----------------------           |--------------------------------------------------------------------------------------------------------------------|
| CHECK_DIGIT_VERIFICATION_FAILED | Applies the Luhn algorithm as described [here](#basic-steps-of-applying-the-luhn-algorithm)                        |
| INPUT_BLANK                     | Validates that the input is not null or blank                                                                      |
| NOT_13_DIGITS                   | Validates that the input is exactly 13 digits long                                                                 |
| INVALID_MONTH_DIGITS            | Validates that the MM part of the input is between 01 and 12                                                       |
| INVALID_DAYS_DIGITS             | Validates that the DD part of the input has the correct number of days according to the year (YY) and (MM) inputs  |

## Response structure

The response of calling the utility will contain a boolean flag (**valid**) indicating whether the ID number passed all validation or not. 

* If the flag is *false*
  * Then the **invalidReason** field will contain the reason CODE as outlined in [here](#validations-performed-in-order)
* If the flag is *true*
  * Then the **invalidReason** field will be null and the **dateOfBirth**, **gender** and **citizenshipStatus** field will be populated with the parsed data.
