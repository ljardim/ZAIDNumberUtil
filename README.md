# South African ID Number utilities

## Structure of a RSA Id number

A South African ID Number is a 13 digit number defined by the following format:

YYMMDDSSSSCAZ

| YYMMDD | Indicates the date of birth of the person.                                                                                      |
|--------|---------------------------------------------------------------------------------------------------------------------------------|
| SSSS   | Sequence used to define the person's gender. Only the first digit is relevant. Females have a number 0 to 4, Males have 5 to 9. |
| C      | 0 if the person is a citizen, 1 if they are a permanent resident.                                                               |
| A      | Was used until the late 1980s to indicate the person's race. Was removed and old ID's have been reissued.                       |
| Z      | Checksum digit. Used to check that the sequence is valid using the Luhn algorithm.                                              |

## Validating a RSA Id number

The last digit of a South African ID number is calculated using the Luhn algorithm, which allows for basic error detection.

To check whether an ID number is valid, the Luhn algorithm may be applied as follows:

1. Working from the rightmost digit of the number, double every second digit.
2. Add the digits of this result together.
3. Sum together the resultant digits, with the remaining (odd) digits of the ID number.
4. If this sum is divisible by 10 (without remainder), the ID number is valid.
