package me.solar.apolloLibrary.utils;


import org.bukkit.util.Vector;

import java.io.Serial;
import java.text.DecimalFormat;
import java.util.*;

public final class MathsUtils {
    private static final DecimalFormat oneDigitFormat = new DecimalFormat("#.#");
    private static final DecimalFormat twoDigitsFormat = new DecimalFormat("#.##");
    private static final DecimalFormat threeDigitsFormat = new DecimalFormat("#.###");
    private static final DecimalFormat fiveDigitsFormat = new DecimalFormat("#.#####");
    private static final NavigableMap<Integer, String> romanNumbers = new TreeMap<>();

    public static String toRoman(int number) {
        if (number == 0) {
            return "0";
        } else {
            int literal = romanNumbers.floorKey(number);
            if (number == literal) {
                return romanNumbers.get(number);
            } else {
                String var10000 = romanNumbers.get(literal);
                return var10000 + toRoman(number - literal);
            }
        }
    }

    public static int max(int... numbers) {
        OptionalInt max = Arrays.stream(numbers).max();

        if (max.isEmpty()) {
            throw new IllegalArgumentException("No numbers given!");
        }

        return max.getAsInt();
    }

    public static double max(double... numbers) {
        OptionalDouble max = Arrays.stream(numbers).max();

        if (max.isEmpty()) {
            throw new IllegalArgumentException("No numbers given!");
        }

        return max.getAsDouble();
    }

    public static int min(int... numbers) {
        OptionalInt min = Arrays.stream(numbers).min();

        if (min.isEmpty()) {
            throw new IllegalArgumentException("No numbers given!");
        }

        return min.getAsInt();
    }

    public static double min(double... numbers) {
        OptionalDouble min = Arrays.stream(numbers).min();

        if (min.isEmpty()) {
            throw new IllegalArgumentException("No numbers given!");
        }

        return min.getAsDouble();
    }

    public static long floor(double d1) {
        long i = (long)d1;
        return d1 >= (double)i ? i : i - 1L;
    }

    public static long ceiling(double f1) {
        return floor(f1);
    }

    public static double range(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static int range(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double atLeast(double value, double min) {
        return Math.max(value, min);
    }

    public static int atLeast(int value, int min) {
        return Math.max(value, min);
    }

    public static int increase(int number, double percent) {
        double percentage = (double) number / (double)100.0F * percent;
        return (int)Math.round((double) number + percentage);
    }

    public static double increase(double number, double percent) {
        double percentage = number / (double)100.0F * percent;
        return number + percentage;
    }

    public static int percent(double number, double maximum) {
        return (int)(number / maximum * (double)100.0F);
    }

    public static double average(Collection<Double> values) {
        return average(values.toArray(new Double[0]));
    }

    public static double average(Double... values) {
        Valid.checkBoolean(values.length > 0, "No values given!");
        double sum = 0.0F;
        int length = values.length;

        for (double val : values) {
            sum += val;
        }

        return formatTwoDigitsD(sum / (double)values.length);
    }

    public static Vector rotateAroundAxisX(Vector vector, double angle) {
        angle = Math.toRadians(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = vector.getY() * cos - vector.getZ() * sin;
        double z = vector.getY() * sin + vector.getZ() * cos;
        return vector.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        angle = Math.toRadians(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public static String formatOneDigit(double value) {
        return oneDigitFormat.format(value).replace(",", ".");
    }

    public static double formatOneDigitD(double value) {
        Valid.checkBoolean(!Double.isNaN(value), "Value must not be NaN");
        return Double.parseDouble(oneDigitFormat.format(value).replace(",", "."));
    }

    public static String formatTwoDigits(double value) {
        return twoDigitsFormat.format(value).replace(",", ".");
    }

    public static double formatTwoDigitsD(double value) {
        Valid.checkBoolean(!Double.isNaN(value), "Value must not be NaN");
        return Double.parseDouble(twoDigitsFormat.format(value).replace(",", "."));
    }

    public static String formatThreeDigits(double value) {
        return threeDigitsFormat.format(value).replace(",", ".");
    }

    public static double formatThreeDigitsD(double value) {
        Valid.checkBoolean(!Double.isNaN(value), "Value must not be NaN");
        return Double.parseDouble(threeDigitsFormat.format(value).replace(",", "."));
    }

    public static String formatFiveDigits(double value) {
        return fiveDigitsFormat.format(value).replace(",", ".");
    }

    public static double formatFiveDigitsD(double value) {
        Valid.checkBoolean(!Double.isNaN(value), "Value must not be NaN");
        return Double.parseDouble(fiveDigitsFormat.format(value).replace(",", "."));
    }

    public static double calculate(final String expression) {
        class Parser {
            int pos = -1;
            int c;

            void eatChar() {
                this.c = ++this.pos < expression.length() ? expression.charAt(this.pos) : -1;
            }

            void eatSpace() {
                while(Character.isWhitespace(this.c)) {
                    this.eatChar();
                }

            }

            double parse() {
                this.eatChar();
                double v = this.parseExpression();
                if (this.c != -1) {
                    throw new CalculatorException("Unexpected: " + (char)this.c);
                } else {
                    return v;
                }
            }

            double parseExpression() {
                double v = this.parseTerm();

                while(true) {
                    this.eatSpace();
                    if (this.c == 43) {
                        this.eatChar();
                        v += this.parseTerm();
                    } else {
                        if (this.c != 45) {
                            return v;
                        }

                        this.eatChar();
                        v -= this.parseTerm();
                    }
                }
            }

            double parseTerm() {
                double v = this.parseFactor();

                while(true) {
                    this.eatSpace();
                    if (this.c == 47) {
                        this.eatChar();
                        v /= this.parseFactor();
                    } else {
                        if (this.c != 42 && this.c != 40) {
                            return v;
                        }

                        if (this.c == 42) {
                            this.eatChar();
                        }

                        v *= this.parseFactor();
                    }
                }
            }

            double parseFactor() {
                boolean negate = false;
                this.eatSpace();
                if (this.c == 43 || this.c == 45) {
                    negate = this.c == 45;
                    this.eatChar();
                    this.eatSpace();
                }

                double v;
                if (this.c == 40) {
                    this.eatChar();
                    v = this.parseExpression();
                    if (this.c == 41) {
                        this.eatChar();
                    }
                } else {
                    StringBuilder sb = new StringBuilder();

                    while(this.c >= 48 && this.c <= 57 || this.c == 46) {
                        sb.append((char)this.c);
                        this.eatChar();
                    }

                    if (sb.isEmpty()) {
                        throw new CalculatorException("Unexpected: " + (char)this.c);
                    }

                    v = Double.parseDouble(sb.toString());
                }

                this.eatSpace();
                if (this.c == 94) {
                    this.eatChar();
                    v = Math.pow(v, this.parseFactor());
                }

                if (negate) {
                    v = -v;
                }

                return v;
            }
        }

        return (new Parser()).parse();
    }

    static {
        romanNumbers.put(1000, "M");
        romanNumbers.put(900, "CM");
        romanNumbers.put(500, "D");
        romanNumbers.put(400, "CD");
        romanNumbers.put(100, "C");
        romanNumbers.put(90, "XC");
        romanNumbers.put(50, "L");
        romanNumbers.put(40, "XL");
        romanNumbers.put(10, "X");
        romanNumbers.put(9, "IX");
        romanNumbers.put(5, "V");
        romanNumbers.put(4, "IV");
        romanNumbers.put(1, "I");
    }

    public static double round(double value) {
        return (int)Math.round(value);
    }

    public static double round(double value, int sf) {
        double factor = Math.pow(10, sf);
        return Math.round(value * factor) / factor;
    }


    public static final class CalculatorException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 1L;

        public CalculatorException(String message) {
            super(message);
        }
    }

}

