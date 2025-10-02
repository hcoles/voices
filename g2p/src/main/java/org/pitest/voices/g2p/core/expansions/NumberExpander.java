package org.pitest.voices.g2p.core.expansions;

import org.pitest.voices.g2p.core.Expansion;

import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberExpander implements Expansion {

    private static final Map<Integer, String> ONES = new HashMap<>();
    private static final Map<Integer, String> TENS = new HashMap<>();
    private static final Map<Long, String> SCALES = new HashMap<>();

    static {
        initializeNumberWords();
    }

    private static void initializeNumberWords() {
        // Initialize ones
        for (int i = 0; i <= 19; i++) {
            switch (i) {
                case 0: ONES.put(i, "zero"); break;
                case 1: ONES.put(i, "one"); break;
                case 2: ONES.put(i, "two"); break;
                case 3: ONES.put(i, "three"); break;
                case 4: ONES.put(i, "four"); break;
                case 5: ONES.put(i, "five"); break;
                case 6: ONES.put(i, "six"); break;
                case 7: ONES.put(i, "seven"); break;
                case 8: ONES.put(i, "eight"); break;
                case 9: ONES.put(i, "nine"); break;
                case 10: ONES.put(i, "ten"); break;
                case 11: ONES.put(i, "eleven"); break;
                case 12: ONES.put(i, "twelve"); break;
                case 13: ONES.put(i, "thirteen"); break;
                case 14: ONES.put(i, "fourteen"); break;
                case 15: ONES.put(i, "fifteen"); break;
                case 16: ONES.put(i, "sixteen"); break;
                case 17: ONES.put(i, "seventeen"); break;
                case 18: ONES.put(i, "eighteen"); break;
                case 19: ONES.put(i, "nineteen"); break;
            }
        }

        // Initialize tens
        for (int i = 20; i <= 90; i += 10) {
            switch (i) {
                case 20: TENS.put(i, "twenty"); break;
                case 30: TENS.put(i, "thirty"); break;
                case 40: TENS.put(i, "forty"); break;
                case 50: TENS.put(i, "fifty"); break;
                case 60: TENS.put(i, "sixty"); break;
                case 70: TENS.put(i, "seventy"); break;
                case 80: TENS.put(i, "eighty"); break;
                case 90: TENS.put(i, "ninety"); break;
            }
        }

        // Initialize scales
        SCALES.put(100L, "hundred");
        SCALES.put(1000L, "thousand");
        SCALES.put(1000000L, "million");
        SCALES.put(1000000000L, "billion");
    }

    @Override
    public String expand(String text) {
        text = expandCurrency(text);

        text = expandYears(text);

        text = expandTimes(text);

        text = expandOrdinals(text);

        text = expandPercentages(text);

        text = expandDecimals(text);

        // Expand regular numbers
        Pattern numberPattern = Pattern.compile("\\b\\d+\\b");
        Matcher numberMatcher = numberPattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        try {
            while (numberMatcher.find()) {
                int num = Integer.parseInt(numberMatcher.group());
                String result = numberToWords(num);
                numberMatcher.appendReplacement(sb, result);
            }
            numberMatcher.appendTail(sb);
            return sb.toString();
        } catch (NumberFormatException e) {
            // not worth supporting numbers outside of int, but should fail semi gracefully
            return text;
        }
    }

    private String expandCurrency(String text) {
        StringBuilder sb = new StringBuilder();
        Pattern currencyPattern = Pattern.compile("£(\\d+(?:,\\d{3})*(?:\\.\\d{2})?)");
        Matcher currencyMatcher = currencyPattern.matcher(text);

        while (currencyMatcher.find()) {
            String amount = currencyMatcher.group(1).replace(",", "");
            double num = Double.parseDouble(amount);
            int dollars = (int) Math.floor(num);
            int cents = (int) Math.round((num - dollars) * 100);

            StringBuilder result = new StringBuilder();
            if (dollars > 0) {
                result.append(numberToWords(dollars)).append(dollars == 1 ? " pound" : " pounds");
            }
            if (cents > 0) {
                if (dollars > 0) result.append(" and ");
                result.append(numberToWords(cents)).append(" pence");
            }
            currencyMatcher.appendReplacement(sb, result.toString());
        }
        currencyMatcher.appendTail(sb);
        return sb.toString();
    }

    private String expandYears(String text) {
        // Expand years (1800-2099)
        StringBuilder sb = new StringBuilder();
        Pattern yearPattern = Pattern.compile("\\b(1[89]\\d{2}|20\\d{2})\\b");
        Matcher yearMatcher = yearPattern.matcher(text);


        while (yearMatcher.find()) {
            int year = Integer.parseInt(yearMatcher.group());
            if (year >= 2000) {
                String result = "twenty ";
                if (year == 2000) {
                    result += "hundred";
                } else if (year < 2010) {
                    result += "oh " + ONES.get(year % 10);
                } else {
                    result += numberToWords(year % 100);
                }
                yearMatcher.appendReplacement(sb, result);
            } else {
                int century = year / 100;
                int remainder = year % 100;
                String result = numberToWords(century) + " ";
                if (remainder < 10) {
                    result += "oh " + ONES.get(remainder);
                } else {
                    result += numberToWords(remainder);
                }
                yearMatcher.appendReplacement(sb, result);
            }
        }
        yearMatcher.appendTail(sb);
        return sb.toString();
    }

    private String expandTimes(String text) {
        // Expand times (12:34, 1:30 AM, etc.)
        Pattern timePattern = Pattern.compile("\\b(\\d{1,2}):(\\d{2})(?:\\s*(am|pm))?\\b", Pattern.CASE_INSENSITIVE);
        Matcher timeMatcher = timePattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (timeMatcher.find()) {
            int h = Integer.parseInt(timeMatcher.group(1));
            int m = Integer.parseInt(timeMatcher.group(2));

            StringBuilder result = new StringBuilder(numberToWords(h == 0 ? 12 : h > 12 ? h - 12 : h));

            if (m == 0) {
                result.append(" o'clock");
            } else if (m < 10) {
                result.append(" oh ").append(numberToWords(m));
            } else {
                result.append(" ").append(numberToWords(m));
            }

            String ampm = timeMatcher.group(3);
            if (ampm != null) {
                result.append(" ").append(ampm.toLowerCase().replaceAll("(\\w)", "$1 "));
            }

            timeMatcher.appendReplacement(sb, result.toString());
        }
        timeMatcher.appendTail(sb);
        return sb.toString();
    }

    private String expandOrdinals(String text) {
        // Expand ordinals (1st, 2nd, 3rd, etc.)
        Pattern ordinalPattern = Pattern.compile("\\b(\\d+)(?:st|nd|rd|th)\\b", Pattern.CASE_INSENSITIVE);
        Matcher ordinalMatcher = ordinalPattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        try {
            while (ordinalMatcher.find()) {
                int num = Integer.parseInt(ordinalMatcher.group(1));
                String result = ordinalToWords(num);
                ordinalMatcher.appendReplacement(sb, result);
            }
        } catch (NumberFormatException e) {
            return text;
        }
        ordinalMatcher.appendTail(sb);
        return sb.toString();
    }


    private String expandDecimals(String text) {
        Pattern pattern = Pattern.compile("\\b(\\d+)\\.(\\d+)\\b");
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        try {
            while (matcher.find()) {
                int whole = Integer.parseInt(matcher.group(1));
                String decimal = matcher.group(2);

                StringBuilder decimalWords = new StringBuilder();
                for (char d : decimal.toCharArray()) {
                    decimalWords.append(ONES.get(d - '0')).append(" ");
                }

                matcher.appendReplacement(result, numberToWords(whole) + " point " + decimalWords.toString().trim());
            }
        } catch (NumberFormatException e) {
            return text;
        }
        matcher.appendTail(result);

        return result.toString();
    }


    private String expandPercentages(String text) {
        // Expand percentages
        Pattern percentPattern = Pattern.compile("\\b(\\d+(?:\\.\\d+)?)%"); // no word boundary at end?
        Matcher percentMatcher = percentPattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (percentMatcher.find()) {
            double n = Double.parseDouble(percentMatcher.group(1));
            StringBuilder result = new StringBuilder(numberToWords((int) Math.floor(n)));
            if (n % 1 != 0) {
                String decimalPart = Double.toString(n).split("\\.")[1];
                result.append(" point");
                for (char c : decimalPart.toCharArray()) {
                    if (result.length() > 0) result.append(" ");
                    result.append(ONES.get(Character.getNumericValue(c)));
                }
            }
            result.append(" percent");
            percentMatcher.appendReplacement(sb, result.toString());
        }
        percentMatcher.appendTail(sb);
        return sb.toString();
    }


    private String numberToWords(int n) {
        if (n == 0) return "zero";
        if (n < 0) return "negative " + numberToWords(-n);

        if (n < 20) return ONES.get(n);
        if (n < 100) {
            int tens = (n / 10) * 10;
            int ones = n % 10;
            return TENS.get(tens) + (ones > 0 ? " " + ONES.get(ones) : "");
        }
        if (n < 1000) {
            int hundreds = n / 100;
            int remainder = n % 100;
            return ONES.get(hundreds) + " hundred" + (remainder > 0 ? " " + numberToWords(remainder) : "");
        }

        // Handle larger numbers
        long[] scales = {1000000000000L, 1000000000, 1000000, 1000};
        for (long scale : scales) {
            if (n >= scale) {
                int quotient = n / (int) scale;
                int remainder = n % (int) scale;
                return numberToWords(quotient) + " " + SCALES.get(scale) + (remainder > 0 ? " " + numberToWords(remainder) : "");
            }
        }

        return Integer.toString(n);
    }

    private String ordinalToWords(int n) {
        String base = numberToWords(n);

        // special cases that don't work with our
        // more general logic
        switch (n) {
            case 12:
                return "twelfth";
            case 15:
                return "fifteenth";
            case 18:
                return "eighteenth";
            case 19:
                return "nineteenth";
            case 20:
                return "twentieth";
            case 30:
                return "thirtieth";
            case 40:
                return "fortieth";
            case 50:
                return "fiftieth";
            case 60:
                return "sixtieth";
            case 70:
                return "seventieth";
            case 80:
                return "eightieth";
            case 90:
                return "ninetieth";
            case 100:
                return "hundredth";
        }

        // Special cases for ordinals
        int mod = n % 100;
        if (mod >= 11 && mod <= 13) {
            return base + "th";
        }

        switch (n % 10) {
            case 1:
                return base.replaceAll("one$", "first");
            case 2:
                return base.replaceAll("two$", "second");
            case 3:
                return base.replaceAll("three$", "third");
            case 5:
                return base.replaceAll("five$", "fifth");
            case 8:
                return base.replaceAll("eight$", "eighth");
            case 9:
                return base.replaceAll("nine$", "ninth");
            default:
                return base + "th";
        }
    }


}

