package fishcute.celestial.util;

import celestialexpressions.Expression;
import celestialexpressions.ExpressionCompiler;
import celestialexpressions.ExpressionContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.version.dependent.Vector;
import fishcute.celestial.version.dependent.VMinecraftInstance;
import fishcute.celestial.version.dependent.VMth;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Util {
    static Random random = new Random();

    static DecimalFormat numberFormat = new DecimalFormat("#.00000000");

    public static Expression compileExpression(String input) {
        try {
            var context = new ExpressionContext();
            context.addModule(CelestialModuleKt.getModule());
            return ExpressionCompiler.compile(input, context);
        } catch (Exception e) {
            sendErrorInGame(e.getMessage(), false);
            return () -> 0.0;
        }
    }

    public static double solveEquation(String str, Map<String, DynamicValue> toReplace) {
        if (toReplace.size() == 0 || str.equals(""))
            return 0;

        // Checks if the string is numeric
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ignored) {
        }

        StringBuilder builder = new StringBuilder(str);

        for (String i : toReplace.keySet()) {
            while (builder.indexOf(i) != -1)
                builder.replace(builder.indexOf(i), builder.indexOf(i) + i.length(), numberFormat.format(Double.valueOf(toReplace.get(i).getValue())));
        }

        // Checks for #isUsing
        for (int index = builder.indexOf("#isUsing"); index >= 0; index = builder.indexOf("#isUsing", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 8);
            if (result != null)
                builder.replace(index, index + result.length() + 10, numberFormat.format(isUsing(result) ? 1 : 0));
            else
                sendErrorInGame("Failed to parse #isUsing variable at index " + index, false);
        }

        // Checks for #isMiningWith
        for (int index = builder.indexOf("#isMiningWith"); index >= 0; index = builder.indexOf("#isMiningWith", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 13);
            if (result != null)
                builder.replace(index, index + result.length() + 15, numberFormat.format(isMiningWith(result) ? 1 : 0));
            else
                sendErrorInGame("Failed to parse #isMiningWith variable at index " + index, false);
        }

        // Checks for #isHolding
        for (int index = builder.indexOf("#isHolding"); index >= 0; index = builder.indexOf("#isHolding", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 10);
            if (result != null)
                builder.replace(index, index + result.length() + 12, numberFormat.format(isHolding(result) ? 1 : 0));
            else
                sendErrorInGame("Failed to parse #isHolding variable at index " + index, false);
        }

        // Checks for #distanceToArea
        for (int index = builder.indexOf("#distanceToArea"); index >= 0; index = builder.indexOf("#distanceToArea", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 15);
            if (result != null)
                builder.replace(index, index + result.length() + 17, numberFormat.format(getDistanceToArea(result)));
            else
                sendErrorInGame("Failed to parse #isInArea variable at index " + index, false);
        }

        // Checks for #isInBiome
        for (int index = builder.indexOf("#isInBiome"); index >= 0; index = builder.indexOf("#isInBiome", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 10);
            if (result != null)
                builder.replace(index, index + result.length() + 12, numberFormat.format(isInBiome(result) ? 1 : 0));
            else
                sendErrorInGame("Failed to parse #isInBiome variable at index " + index, false);
        }

        // Checks for #distanceToBiomeIgnoreY
        for (int index = builder.indexOf("#distanceToBiomeIgnoreY"); index >= 0; index = builder.indexOf("#distanceToBiomeIgnoreY", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 23);
            if (result != null)
                builder.replace(index, index + result.length() + 25, numberFormat.format(getBiomeBlendIgnoreY(result)));
            else
                sendErrorInGame("Failed to parse #distanceToBiomeIgnoreY variable at index " + index, false);
        }

        // Checks for #distanceToBiome
        for (int index = builder.indexOf("#distanceToBiome"); index >= 0; index = builder.indexOf("#distanceToBiome", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 16);
            if (result != null)
                builder.replace(index, index + result.length() + 18, numberFormat.format(getBiomeBlend(result)));
            else
                sendErrorInGame("Failed to parse #distanceToBiome variable at index " + index, false);
        }

        // Checks for #distanceTo
        for (int index = builder.indexOf("#distanceTo"); index >= 0; index = builder.indexOf("#distanceTo", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 11);
            if (result != null)
                builder.replace(index, index + result.length() + 13, numberFormat.format(distanceTo(result)));
            else
                sendErrorInGame("Failed to parse #distanceTo variable at index " + index, false);
        }

        // Checks for #isInArea
        for (int index = builder.indexOf("#isInArea"); index >= 0; index = builder.indexOf("#isInArea", index + 1)) {
            result = parseStringVariable(builder.toString(), index, 9);
            if (result != null)
                builder.replace(index, index + result.length() + 11, numberFormat.format(isInArea(result) ? 1 : 0));
            else
                sendErrorInGame("Failed to parse #isInArea variable at index " + index, false);
        }

        // Checks again if the string is completely numeric
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ignored) {
        }

        String finalStr = builder.toString();

        return new Equation(finalStr).parse();
    }

    static String result;
    static int foundIndex;

    static String parseStringVariable(String str, int index, int functionLength) {
        for (foundIndex = index; foundIndex < str.length() - 1; foundIndex++)
            if (str.charAt(foundIndex) == ')') {
                break;
            } else if (foundIndex == str.length() - 1) {
                foundIndex = -1;
                break;
            }

        if (foundIndex > 0 && ((index + functionLength + 1) < foundIndex)) {
            return str.substring(index + functionLength + 1, foundIndex);
        }
        return null;
    }

    /*
    Function below originally made by Boann on StackOverFlow, and slightly modified by me.
    */
    private static class Equation {
        public Equation(String finalStr) {
            this.finalStr = finalStr;
        }

        final String finalStr;
        boolean foundIssue = false;
        int pos = -1, ch;

        void nextChar() {
            ch = (++pos < finalStr.length()) ? finalStr.charAt(pos) : -1;
        }

        boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            nextChar();
            double x = parseExpression();
            if (pos < finalStr.length()) {
                if (!foundIssue) {
                    sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Unexpected character '" + (char) ch + "'", false);
                    foundIssue = true;
                }
                return 0;
            }
            if (foundIssue)
                return 0;
            return x;
        }

        double parseExpression() {
            double x = parseTerm();
            for (; ; ) {
                if (eat('+')) x += parseTerm(); // addition
                else if (eat('-')) x -= parseTerm(); // subtraction
                else return x;
            }
        }

        double parseTerm() {
            double x = parseFactor();
            for (; ; ) {
                if (eat('*')) x *= parseFactor(); // multiplication
                else if (eat('/')) x /= parseFactor(); // division
                else return x;
            }
        }

        double parseFactor() {
            if (eat('+')) return +parseFactor(); // unary plus
            if (eat('-')) return -parseFactor(); // unary minus

            double x;
            int startPos = this.pos;
            if (eat('(')) { // parentheses
                x = parseExpression();
                if (!eat(')')) {
                    if (!foundIssue) {
                        sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Missing closing parenthesis in function", false);
                        foundIssue = true;
                    }
                    return 0;
                }
            } else if ((ch >= '0' && ch <= '9') || ch == '.' || ch == ',') { // numbers
                while ((ch >= '0' && ch <= '9') || ch == '.' || ch == ',') nextChar();
                x = parseDouble(finalStr.substring(startPos, this.pos));
            } else if (ch >= 'a' && ch <= 'z') { // functions
                while (ch >= 'a' && ch <= 'z') nextChar();
                String func = finalStr.substring(startPos, this.pos);
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) {
                        if (!foundIssue) {
                            sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Missing closing parenthesis in function after argument to \"" + func + "\"", false);
                            foundIssue = true;
                        }
                        return 0;
                    }
                } else {
                    x = parseFactor();
                }

                switch (func) {
                    case "sqrt":
                        x = Math.sqrt(x);
                        break;
                    case "sin":
                        x = Math.sin(Math.toRadians(x));
                        break;
                    case "cos":
                        x = Math.cos(Math.toRadians(x));
                        break;
                    case "tan":
                        x = Math.tan(Math.toRadians(x));
                    case "arctan":
                        x = Math.atan(Math.toRadians(x));
                    case "arcsin":
                        x = Math.asin(Math.toRadians(x));
                    case "arccos":
                        x = Math.acos(Math.toRadians(x));
                    case "abs":
                        x = Math.abs(x);
                        break;
                    case "radians":
                        x = Math.toRadians(x);
                        break;
                    case "floor":
                        x = Math.floor(x);
                        break;
                    case "ceil":
                        x = Math.ceil(x);
                        break;
                    case "round":
                        x = Math.round(x);
                        break;
                    case "print":
                        print(x);
                        break;
                    case "printnv":
                        print(x);
                        x = 0;
                        break;
                    default: {
                        if (!foundIssue) {
                            sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Unknown math function \"" + func + "\"", false);
                            foundIssue = true;
                        }
                        return 0;
                    }
                }
            } else {
                if (!foundIssue) {
                    sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Unexpected character '" + (char) ch + "'", false);
                    foundIssue = true;
                }
                return 0;
            }

            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
            else if (eat('m')) x = Math.min(x, parseFactor()); // min
            else if (eat('M')) x = Math.max(x, parseFactor()); // max
            else if (eat('&')) x = (x == 1 && parseFactor() == 1) ? 1 : 0; // and
            else if (eat('|')) x = (or(x, parseFactor())) ? 1 : 0; // or
            else if (eat('=')) x = (x == parseFactor()) ? 1 : 0; // and
            else if (eat('>')) x = (x > parseFactor()) ? 1 : 0;
            else if (eat('<')) x = (x < parseFactor()) ? 1 : 0;

            return x;
        }
    }

    // For some reason things won't work if I don't do this
    static boolean or(double a, double b) {
        return a == 1 || b == 1;
    }

    static double parseDouble(String i) {
        return Double.parseDouble(i.contains(",") ? i.replaceAll(",", ".") : i);
    }

    static double print(double i) {
        VMinecraftInstance.sendMessage("Value: " + i, true);
        return i;
    }

    public static void log(Object i) {
        if (!VMinecraftInstance.isGamePaused())
            System.out.println("[Celestial] " + i.toString());
    }

    public static void warn(Object i) {
        CelestialSky.warnings++;
        if (!VMinecraftInstance.isGamePaused()) {
            log("[Warn] " + i.toString());
            sendWarnInGame(i.toString());
        }
    }

    public static ArrayList<String> errorList = new ArrayList<>();

    public static void sendErrorInGame(String i, boolean unloadResources) {
        CelestialSky.errors++;
        if (!VMinecraftInstance.doesPlayerExist())
            return;
        if (errorList.contains(i) || errorList.size() > 25)
            return;
        errorList.add(i);
        VMinecraftInstance.sendErrorMessage("[Celestial] " + i);

        if (errorList.size() >= 25)
            VMinecraftInstance.sendErrorMessage("[Celestial] Passing 25 error messages. Muting error messages.");

        if (unloadResources) {
            VMinecraftInstance.sendErrorMessage("[Celestial] Unloading Celestial resources.");
        }
    }

    public static void sendWarnInGame(String i) {
        if (!VMinecraftInstance.doesPlayerExist())
            return;
        if (errorList.contains(i))
            return;
        errorList.add(i);
        VMinecraftInstance.sendErrorMessage("[Celestial] " + i);
    }

    public static boolean getOptionalBoolean(JsonObject o, String toGet, boolean ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsBoolean() : ifNull;
    }

    public static String getOptionalString(JsonObject o, String toGet, String ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsString() : ifNull;
    }

    public static double getOptionalDouble(JsonObject o, String toGet, double ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsDouble() : ifNull;
    }

    public static int getOptionalInteger(JsonObject o, String toGet, int ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsInt() : ifNull;
    }

    public static ArrayList<String> getOptionalStringArray(JsonObject o, String toGet, ArrayList<String> ifNull) {
        return o != null && o.has(toGet) ? convertToStringArrayList(o.get(toGet).getAsJsonArray()) : ifNull;
    }

    public static ArrayList<String> convertToStringArrayList(JsonArray array) {
        ArrayList<String> toReturn = new ArrayList<>();
        for (JsonElement o : array) {
            toReturn.add(o.getAsString());
        }
        return toReturn;
    }

    public static int getDecimal(Color color) {
        return color.getRGB();
    }

    public static double generateRandomDouble(double min, double max) {
        return min + ((max - min) * random.nextDouble());
    }

    public static Color decodeColor(String hex) {
        try {
            switch (hex) {
                case "#skyColor":
                    return Util.getSkyColor();
                case "#fogColor":
                    return Util.getFogColor();
            }
            return Color.decode(hex.startsWith("#") ? hex : "#" + hex);
        } catch (Exception ignored) {
            sendErrorInGame("Failed to parse HEX color \"" + hex + "\"", false);
            return new Color(0, 0, 0);
        }
    }

    static final HashMap<String, DynamicValue> toReplaceMap = new HashMap<>();

    public static void initalizeToReplaceMap(HashMap<String, DynamicValue> extraValues) {
        //new DynamicValue() {@Override double getValue() {return ;}};

        toReplaceMap.clear();

        toReplaceMap.put("#xPos", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getPlayerX();
            }
        });
        toReplaceMap.put("#yPos", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getPlayerY();
            }
        });
        toReplaceMap.put("#zPos", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getPlayerZ();
            }
        });
        toReplaceMap.put("#tickDelta", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getTickDelta();
            }
        });
        toReplaceMap.put("#dayLight", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getDayLight();
            }
        });
        toReplaceMap.put("#rainGradient", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        1.0F - VMinecraftInstance.getRainLevel();
            }
        });
        toReplaceMap.put("#isSubmerged", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.isPlayerInWater() ? 1 : 0;
            }
        });
        toReplaceMap.put("#getGameTime", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getGameTime();
            }
        });
        toReplaceMap.put("#getWorldTime", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getWorldTime();
            }
        });
        toReplaceMap.put("#getDayTime", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getWorldTime() - (Math.floor(VMinecraftInstance.getWorldTime() / 24000f) * 24000);
            }
        });
        toReplaceMap.put("#starAlpha", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getStarBrightness();
            }
        });
        toReplaceMap.put("#random", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        Math.random();
            }
        });
        toReplaceMap.put("#skyAngle", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getTimeOfDay() * 360.0F;
            }
        });
        toReplaceMap.put("#maxInteger", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        Integer.MAX_VALUE;
            }
        });
        toReplaceMap.put("#pi", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        Math.PI;
            }
        });
        toReplaceMap.put("#headYaw", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getViewXRot();
            }
        });
        toReplaceMap.put("#headPitch", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getViewYRot();
            }
        });
        toReplaceMap.put("#isLeftClicking", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        isLeftClicking() ? 1 : 0;
            }
        });
        toReplaceMap.put("#isRightClicking", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        isRightClicking() ? 1 : 0;
            }
        });
        toReplaceMap.put("#viewDistance", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getRenderDistance();
            }
        });
        toReplaceMap.put("#moonPhase", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getMoonPhase();
            }
        });
        toReplaceMap.put("#localDayOfYear", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTime(0);
            }
        });
        toReplaceMap.put("#localDayOfMonth", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTime(1);
            }
        });
        toReplaceMap.put("#localDayOfWeek", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTime(2);
            }
        });
        toReplaceMap.put("#localMonth", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTime(3);
            }
        });
        toReplaceMap.put("#localYear", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTime(4);
            }
        });
        toReplaceMap.put("#localSecondOfHour", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTime(5);
            }
        });
        toReplaceMap.put("#localMinuteOfHour", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTime(6);
            }
        });
        toReplaceMap.put("#localMillisecondOfDay", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTotalMilliseconds();
            }
        });
        toReplaceMap.put("#localSecondOfDay", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTotalSeconds();
            }
        });
        toReplaceMap.put("#localMinuteOfDay", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        getTotalMinutes();
            }
        });
        toReplaceMap.put("#localHour", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        LocalDate.now().atTime(LocalTime.now()).getHour();
            }
        });
        toReplaceMap.put("#skyDarken", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getSkyDarken();
            }
        });
        toReplaceMap.put("#lightningFlashTime", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getSkyFlashTime();
            }
        });
        toReplaceMap.put("#thunderGradient", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getThunderLevel();
            }
        });
        toReplaceMap.put("#skyLightLevel", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getSkyLight();
            }
        });
        toReplaceMap.put("#blockLightLevel", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getBlockLight();
            }
        });
        toReplaceMap.put("#twilightAlpha", new DynamicValue() {
            @Override
            public double getValue() {
                float g = VMth.cos(VMinecraftInstance.getTimeOfDay() * 6.2831855F) - 0.0F;
                if (g >= -0.4F && g <= 0.4F)
                    return Math.pow(1.0F - (1.0F - VMth.sin(((g + 0.0F) / 0.4F * 0.5F + 0.5F) * 3.1415927F)) * 0.99F, 2);
                return 0;
            }
        });
        toReplaceMap.put("#biomeTemperature", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getBiomeTemperature();
            }
        });
        toReplaceMap.put("#biomeDownfall", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getBiomeDownfall();
            }
        });
        toReplaceMap.put("#biomeHasSnow", new DynamicValue() {
            @Override
            public double getValue() {
                return
                        VMinecraftInstance.getBiomeSnow() ? 1 : 0;
            }
        });

        toReplaceMap.putAll(extraValues);
    }

    public static int getTime(int id) {
        switch (id) {
            case 0:
                return LocalDate.now().getDayOfYear();
            case 1:
                return LocalDate.now().getDayOfMonth();
            case 2:
                return LocalDate.now().getDayOfWeek().getValue();
            case 3:
                return LocalDate.now().getMonthValue();
            case 4:
                return LocalDate.now().getYear();
            case 5:
                return LocalDate.now().atTime(LocalTime.now()).getSecond();
            case 6:
                return LocalDate.now().atTime(LocalTime.now()).getMinute();
            default:
                return 0;
        }
    }

    public static long getTotalMilliseconds() {
        return (Calendar.getInstance().getTimeInMillis() + Calendar.getInstance().get(Calendar.ZONE_OFFSET) +
                Calendar.getInstance().get(Calendar.DST_OFFSET)) %
                (24 * 60 * 60 * 1000);
    }

    public static int getTotalSeconds() {
        return (getTotalMinutes() * 60) + LocalDate.now().atTime(LocalTime.now()).getSecond();
    }

    public static int getTotalMinutes() {
        return (LocalDate.now().atTime(LocalTime.now()).getHour() * 60) + LocalDate.now().atTime(LocalTime.now()).getMinute();
    }

    public static HashMap<String, DynamicValue> getReplaceMapNormal() {
        return toReplaceMap;
    }

    public static HashMap<String, DynamicValue> getReplaceMapAdd(Map<String, Double> extraEntries) {
        HashMap<String, DynamicValue> toReturn = (HashMap<String, DynamicValue>) getReplaceMapNormal().clone();
        for (String i : extraEntries.keySet()) {
            toReturn.put(i, new DynamicValue() {
                @Override
                public double getValue() {
                    return
                            extraEntries.get(i);
                }
            });
        }
        return toReturn;
    }

    public static abstract class DynamicValue {
        public abstract double getValue();
    }

    public static class MutableDynamicValue extends DynamicValue {
        public double value;

        public double getValue() {
            return this.value;
        }

        public MutableDynamicValue() {
            this.value = 0;
        }

        public MutableDynamicValue(double value) {
            this.value = value;
        }

        public MutableDynamicValue clone() {
            return new MutableDynamicValue(this.value);
        }
    }

    public static ArrayList<VertexPoint> convertToPointUvList(JsonObject o, String name) {

        ArrayList<VertexPoint> returnList = new ArrayList<>();
        try {
            if (!o.has(name))
                return new ArrayList<>();
            for (JsonElement e : o.getAsJsonArray(name)) {
                JsonObject entry = e.getAsJsonObject();
                returnList.add(
                        new VertexPoint(
                                getOptionalString(entry, "x", ""),
                                getOptionalString(entry, "y", ""),
                                getOptionalString(entry, "z", ""),
                                getOptionalString(entry, "uv_x", null),
                                getOptionalString(entry, "uv_y", null)
                        )
                );
            }
        } catch (Exception e) {
            sendErrorInGame("Failed to parse vertex point list \"" + name + "\".", false);
            return new ArrayList<>();
        }

        return returnList;
    }

    public static class VertexPoint {
        public Expression pointX;
        public Expression pointY;
        public Expression pointZ;
        public Expression uvX;
        public Expression uvY;

        public boolean hasUv;

        public VertexPoint(String pointX, String pointY, String pointZ, String uvX, String uvY) {
            this.pointX = Util.compileExpression(pointX);
            this.pointY = Util.compileExpression(pointY);
            this.pointZ = Util.compileExpression(pointZ);

            this.hasUv = uvX != null || uvY != null;

            this.uvX = Util.compileExpression(uvX);
            this.uvY = Util.compileExpression(uvY);
        }
    }

    public static class VertexPointValue {
        public double pointX;
        public double pointY;
        public double pointZ;
        public double uvX = 0;
        public double uvY = 0;

        public boolean hasUv;

        public VertexPointValue(VertexPoint point) {
            this.pointX = point.pointX.invoke().floatValue();
            this.pointY = point.pointY.invoke().floatValue();
            this.pointZ = point.pointZ.invoke().floatValue();

            this.hasUv = point.hasUv;

            if (this.hasUv) {
                this.uvX = point.uvX.invoke().floatValue();
                this.uvY = point.uvY.invoke().floatValue();
            }
        }
    }

    public static boolean isUsing(String item) {
        return VMinecraftInstance.isRightClicking() && isHolding(item);
    }

    public static boolean isMiningWith(String item) {
        return VMinecraftInstance.isLeftClicking() && isHolding(item);
    }

    public static boolean isRightClicking() {
        return VMinecraftInstance.isRightClicking();
    }

    public static boolean isLeftClicking() {
        return VMinecraftInstance.isLeftClicking();
    }

    public static boolean isHolding(String item) {
        if (item.contains(":")) {
            String[] str = item.split(":");
            return (VMinecraftInstance.getMainHandItemNamespace().equals(str[0])) &&
                    (VMinecraftInstance.getMainHandItemPath().equals(str[1]));
        } else {
            return (VMinecraftInstance.getMainHandItemPath().equals(item));
        }
    }

    public static boolean isInArea(String arguments) {
        try {
            String[] str = arguments.split(",");

            return (Double.parseDouble(str[0]) <= VMinecraftInstance.getPlayerX() && VMinecraftInstance.getPlayerX() <= Double.parseDouble(str[3]) + 1) &&
                    (Double.parseDouble(str[1]) <= VMinecraftInstance.getPlayerY() && VMinecraftInstance.getPlayerY() <= Double.parseDouble(str[4]) + 1) &&
                    (Double.parseDouble(str[2]) <= VMinecraftInstance.getPlayerZ() && VMinecraftInstance.getPlayerZ() <= Double.parseDouble(str[5]) + 1);
        } catch (Exception e) {
            sendErrorInGame("Failed to parse #isInArea variable with arguments \"" + arguments + "\".", false);
            return false;
        }
    }

    public static double distanceTo(String arguments) {
        try {
            String[] str = arguments.split(",");

            return distanceTo(VMinecraftInstance.getPlayerX(), VMinecraftInstance.getPlayerY(), VMinecraftInstance.getPlayerZ(),
                    Double.parseDouble(str[0]), Double.parseDouble(str[1]), Double.parseDouble(str[2]));
        } catch (Exception e) {
            sendErrorInGame("Failed to parse #distanceTo variable with arguments \"" + arguments + "\".", false);
            return 0;
        }
    }

    public static double distanceTo(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
    }

    static float getDistance(float a, float b) {
        if (a <= 0) {
            if (b <= 0) return 0F;
            else return b;
        } else if (b <= 0)
            return a;
        return ((float) Math.sqrt(((double) a * a + b * b)));
    }

    public static double getDistanceToArea(String arguments) {
        try {
            String[] str = arguments.split(",");

            return getDistanceToArea(
                    Double.parseDouble(str[0]),
                    Double.parseDouble(str[1]),
                    Double.parseDouble(str[2]),
                    Double.parseDouble(str[3]),
                    Double.parseDouble(str[4]),
                    Double.parseDouble(str[5]));
        } catch (Exception e) {
            sendErrorInGame("Failed to parse #distanceToArea variable with arguments \"" + arguments + "\".", false);
            return 0;
        }
    }

    public static double getDistanceToArea(double x1, double y1, double z1, double x2, double y2, double z2) {
        double minX = Float.min((float) x1, (float) x2);
        double maxX = Float.max((float) x1, (float) x2);
        double minY = Float.min((float) y1, (float) y2);
        double maxY = Float.max((float) y1, (float) y2);
        double minZ = Float.min((float) z1, (float) z2);
        double maxZ = Float.max((float) z1, (float) z2);

        float[] axisDistances = new float[3];

        {
            double min = minX - VMinecraftInstance.getPlayerX();
            double max = VMinecraftInstance.getPlayerX() - maxX;
            axisDistances[0] = Float.max((float) min, (float) max);
        }
        {
            double min = minY - VMinecraftInstance.getPlayerY();
            double max = VMinecraftInstance.getPlayerY() - maxY;
            axisDistances[1] = Float.max((float) min, (float) max);
        }
        {
            double min = minZ - VMinecraftInstance.getPlayerZ();
            double max = VMinecraftInstance.getPlayerZ() - maxZ;
            axisDistances[2] = Float.max((float) min, (float) max);
        }

        return getDistance(
                getDistance(
                        axisDistances[0],
                        axisDistances[1]
                ),
                axisDistances[2]
        );
    }

    public static double getBiomeBlend(String arguments) {
        String[] a = arguments.split(",");
        try {
            if (a.length == 1)
                return getBiomeBlend(a[0], 6);
            return getBiomeBlend(a[0], Integer.parseInt(a[1].replaceAll("\\s", "")));
        } catch (Exception e) {
            sendErrorInGame("Failed to parse arguments \"" + arguments + "\" for #distanceToBiome variable.", false);
            return 0;
        }
    }

    public static double getBiomeBlend(String biomeName, int searchDistance) {
        if (isInBiome(biomeName))
            return 1;
        boolean foundSpot = false;
        double dist;
        double closestDist = searchDistance;
        Vector pos = new Vector(0, 0, 0);
        for (int i = -searchDistance; i <= searchDistance; i++) {
            for (int j = -searchDistance; j <= searchDistance; j++) {
                for (int k = -searchDistance; k <= searchDistance; k++) {
                    pos.set(i + VMinecraftInstance.getPlayerX(),
                            j + VMinecraftInstance.getPlayerY(),
                            k + VMinecraftInstance.getPlayerZ());
                    dist = distanceTo(VMinecraftInstance.getPlayerX(), VMinecraftInstance.getPlayerY(), VMinecraftInstance.getPlayerZ(),
                            VMinecraftInstance.getPlayerX(),
                            VMinecraftInstance.getPlayerY(),
                            VMinecraftInstance.getPlayerZ());
                    if (VMinecraftInstance.equalToBiome(pos, biomeName) && (!foundSpot || dist < closestDist)) {
                        closestDist = getDistanceToArea(pos.x - 0.5, pos.y - 0.5, pos.z + 0.5, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
                        foundSpot = true;
                    }
                }
            }
        }

        if (foundSpot) {
            double a = 0.05 * searchDistance;
            a = 0.2 + Math.max(a, 0.25);
            closestDist = ((closestDist - a) / searchDistance) > 1 ? 1 : ((closestDist - a) / searchDistance);
            closestDist = closestDist < 0 ? 0 : closestDist;
            closestDist = 1 - closestDist;
            return closestDist;
        }
        return 0;
    }

    public static double getBiomeBlendIgnoreY(String arguments) {
        String[] a = arguments.split(",");
        try {
            if (a.length == 1)
                return getBiomeBlendIgnoreY(a[0], 6, VMinecraftInstance.getPlayerY());
            else if (a.length == 2)
                return getBiomeBlendIgnoreY(a[0], Integer.parseInt(a[1].replaceAll("\\s", "")), VMinecraftInstance.getPlayerY());
            return getBiomeBlendIgnoreY(a[0], Integer.parseInt(a[1].replaceAll("\\s", "")), Double.parseDouble(a[2].replaceAll("\\s", "")));
        } catch (Exception e) {
            sendErrorInGame("Failed to parse arguments \"" + arguments + "\" for #distanceToBiomeIgnoreY variable.", false);
            return 0;
        }
    }

    public static double getBiomeBlendIgnoreY(String biomeName, int searchDistance, double yLevel) {
        if (isInBiome(biomeName))
            return 1;
        boolean foundSpot = false;
        double dist;
        double closestDist = searchDistance;
        Vector pos = new Vector(0, 0, 0);
        for (int i = -searchDistance; i <= searchDistance; i++) {
            for (int k = -searchDistance; k <= searchDistance; k++) {
                pos.set(i + VMinecraftInstance.getPlayerX(), yLevel, k + VMinecraftInstance.getPlayerZ());
                dist = distanceTo(pos.x, yLevel, pos.z,
                        VMinecraftInstance.getPlayerX(),
                        yLevel,
                        VMinecraftInstance.getPlayerZ());
                if (VMinecraftInstance.equalToBiome(pos, biomeName) && (!foundSpot || dist < closestDist)) {
                    closestDist = dist;
                    foundSpot = true;
                }
            }
        }

        if (foundSpot) {
            double a = 0.05 * searchDistance;
            a = 0.2 + Math.max(a, 0.25);
            closestDist = ((closestDist - a) / searchDistance) > 1 ? 1 : ((closestDist - a) / searchDistance);
            closestDist = closestDist < 0 ? 0 : closestDist;
            closestDist = 1 - closestDist;
            return closestDist;
        }
        return 0;
    }

    //TODO: Allow for simple biome names (eg. plains, swamp, not minecraft:plains)

    public static boolean isInBiome(String biome) {
        return VMinecraftInstance.equalToBiome(null, biome);
    }

    public static boolean getRealSkyColor = false;
    public static boolean getRealFogColor = false;

    public static Color getSkyColor() {
        double[] i = VMinecraftInstance.getBiomeSkyColor();
        return new Color((int) (i[0] * 255), (int) (i[1] * 255), (int) (i[2] * 255));
    }

    public static Color getFogColor() {
        double[] i = VMinecraftInstance.getBiomeFogColor();
        return new Color((int) (i[0] * 255), (int) (i[1] * 255), (int) (i[2] * 255));
    }

    public static Color getWaterFogColor() {
        double[] i = VMinecraftInstance.getBiomeWaterFogColor();
        return new Color((int) (i[0] * 255), (int) (i[1] * 255), (int) (i[2] * 255));
    }

    public static float getDayLight() {
        return 1 - (float) (1 + ((VMinecraftInstance.getStarBrightness() - 0.5) * 2));
    }


}
