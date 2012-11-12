//Funkcja.java
package arkusz;

/**
 *
 * @author Łukasz Świderski i Karol Pawluczuk
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.script.ScriptException;

public class Funkcja {

    /**
     * Funkcja sprawdza czy podany String jest liczbą.
     *
     * @param token Kandydat na liczbę
     * @return true jeśli String jest liczbą / false w przeciwnym wypadku
     */
    private static boolean isNumber(final String token) {
        try {
            Double.valueOf(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Funkcja pozyskuje adresy wszystkich komórek z określonego przedziału.
     * Otrzymany ciąg dzieli na poszczególne znaki i przekształca adresy
     * literowoliczbowe na adresy x y po czym łapie wszystkie komórki w
     * przydziale i ponownie zapisuje ich adresy w postaci literowoliczbowej
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param ciag zakres komórek
     * @return zwraca ArrayList zawierającą adresy komórek z pozyskanego
     * przedziału
     */
    static ArrayList<String> getAdresy(Komorka k, String ciag) {
        StringTokenizer stc = new StringTokenizer(ciag, " :");
        String token;

        String x1 = "", x2 = "", y1 = "", y2 = "";
        ArrayList<String> adresy = new ArrayList<String>();
        token = stc.nextToken();
        if (k.sprawdzAdres(token)) {
            for (int i = 0; i < token.length(); i++) {
                if (isNumber(Character.toString(token.charAt(i)))) {
                    y1 += Character.toString(token.charAt(i));
                } else {
                    x1 += Character.toString(token.charAt(i));
                }
            }
        } else {
            return adresy;
        }

        token = stc.nextToken();
        if (k.sprawdzAdres(token)) {
            for (int i = 0; i < token.length(); i++) {
                if (isNumber(Character.toString(token.charAt(i)))) {
                    y2 += Character.toString(token.charAt(i));
                } else {
                    x2 += Character.toString(token.charAt(i));
                }
            }
        } else {
            return adresy;
        }


        int ix1 = (int) (x1.charAt(0));
        int ix2 = (int) (x2.charAt(0));
        int iy1 = (int) Integer.parseInt(y1);
        int iy2 = (int) Integer.parseInt(y2);

        String zzakresu = "";


        int ilosc_adresow = 0;
        for (int i = ix1; i <= ix2; i++) {
            for (int j = iy1; j <= iy2; j++) {

                zzakresu += (char) i;
                zzakresu += Integer.toString(j);

                adresy.add(zzakresu);
                ilosc_adresow++;
                zzakresu = "";
            }
        }

        return adresy;
    }

    /**
     * Funkcja do obliczania funkcji tzw zakresowych. z podanego ciągu pozysuje
     * adresy za pomocą funkcji getAdresy Następnie przekazuje je do
     * poszczególnych funkcji w zależności jaka zastała wybrana i zwraca wynik
     * owej funkcji
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param ciag parametry funkcji zakresowej, prawdopodobnie zakres
     * @param ktora wybrana funkcja
     * @return wynik prostej funkcji matematycznej
     * @throws ScriptException
     */
    static String fzakresowa(Komorka k, String ciag, int ktora) throws ScriptException {

        ArrayList<String> adresy = getAdresy(k, ciag);
        if (adresy.isEmpty()) {
            return "błąd";
        }


        switch (ktora) {
            //suma
            case 101: {
                return suma(k, adresy);
            }
            //srednia arytmetyczna
            case 102: {
                return srednia(k, adresy);
            }
            case 103: {
                return max(k, adresy);
            }
            case 104: {
                return min(k, adresy);
            }
            case 105: {
                return mediana(k, adresy);
            }
            case 106: {
                return iloczyn(k, adresy);
            }
            case 107: {
                return roznica(k, adresy);
            }
            case 108: {
                return sort(k, adresy);
            }
            default:
                return "bład";
        }
    }

    /**
     * Funkcja obsługująca w założeniu proste funckje jednoparametrowe. Jednakże
     * jako parametr może być podane wyrażenie arytmetyczne lub inna funkcja. Z
     * tego powodu funkcja musi wywołąć funkcje eval z klasy Komorka by
     * rozwiązać podane wyraenie i dopiero przekazuje wynik do prostej funkcji
     * wyranej przez użytkownika
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param ciag parametr funkcji (wyrazenie)
     * @param ktora wybrana funkcja
     * @return wynik prostej funkcji matematycznej
     */
    static String fMatProsta(Komorka k, String ciag, int ktora) throws ScriptException {
        StringTokenizer stc = new StringTokenizer(ciag, " +-*/()");
        String token;
        String subform;
        while (stc.hasMoreTokens()) {
            token = stc.nextToken();
            if (k.funkcje.containsKey(token)) {

                String ciag2 = ciag.substring(ciag.indexOf(token));

                StringBuffer nciag = new StringBuffer(ciag2);

                nciag = nciag.delete(ciag2.indexOf(token), ciag2.length());

                ciag = nciag.toString() + k.eval(ciag2);


            }
            if (isNumber(token)) {
                subform = token;
            } else {
                subform = k.props.getProperty(token);
                if (subform == null) {
                    subform = "0";

                }

                subform = k.eval(subform);
                ciag = ciag.replaceAll(token, subform);
            }
        }

        String str;
        try {
            str = k.engine.eval(ciag).toString();


        } catch (ScriptException e) {
            str = "0";
        }

        double d = Double.valueOf(str).doubleValue();

        switch (ktora) {
            //sinus
            case 1: {
                return Double.toString(Math.sin(Math.toRadians(d)));

            }
            //cosinus
            case 2: {
                return Double.toString(Math.cos(Math.toRadians(d)));
            }
            //tangens
            case 3: {
                return Double.toString(Math.tan(Math.toRadians(d)));
            }
            //cotangens
            case 4: {
                return Double.toString(Math.tan(Math.toRadians(1 / d)));
            }
            //arcus sinus
            case 5: {
                return Double.toString(Math.asin(Math.toRadians(d)));

            }
            //arcus cosinus
            case 6: {
                return Double.toString(Math.acos(Math.toRadians(d)));
            }
            //arcus tangens
            case 7: {
                return Double.toString(Math.atan(Math.toRadians(d)));
            }
            //arcus cotangens
            case 8: {
                return Double.toString(Math.atan(Math.toRadians(1 / d)));
            }
            case 9: {
                return Double.toString(Math.sin(d));

            }
            //cosinus
            case 10: {
                return Double.toString(Math.cos(d));
            }
            //tangens
            case 11: {
                return Double.toString(Math.tan(d));
            }
            //cotangens
            case 12: {
                return Double.toString(Math.tan(1 / d));
            }
            //arcus sinus
            case 13: {
                return Double.toString(Math.asin(d));
            }
            //arcus cosinus
            case 14: {
                return Double.toString(Math.acos(d));
            }
            //arcus tangens
            case 15: {
                return Double.toString(Math.atan(d));
            }
            //arcus cotangens
            case 16: {
                return Double.toString(Math.atan(1 / d));
            }
            //silnia
            case 17: {
                return Double.toString(factorial((int) d));
            }
            //modul
            case 18: {
                return Double.toString(Math.abs(d));
            }
            case 19: {
                return Double.toString(Math.sqrt(d));
            }

            default:
                return "błąd";

        }
    }

    /**
     * Funckja obliczająca silnie z podanej liczby
     *
     * @param n liczba int
     * @return silnia z n
     */
    public static long factorial(int n) {

        if (n <= 1) // base case
        {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    /*
     * -----------------------------------------
     */
    /**
     * Funkcja obsługująca funkcje 2 argumentowe. Parametry funkcji powinny być
     * oddzielone znakiem ; Funkcja wykonuje funkcję wybraną przez użytkownika
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param ciag argumenty funckji
     * @param ktora funckja wybrana przez użytkownika
     * @return
     */
    static String dwuArgumentowa(Komorka k, String ciag, int ktora) {
        StringTokenizer stc = new StringTokenizer(ciag, " ;:()");
        String token1, token2;
        token1 = stc.nextToken();
        token2 = stc.nextToken();
        if (isNumber(token1)); else if (k.sprawdzAdres(token1)) {
            token1 = k.props.getProperty(token1);
        } else {
            return "błąd";
        }

        if (isNumber(token2)); else if (k.sprawdzAdres(token2)) {
            token2 = k.props.getProperty(token2);
        } else {
            return "błąd";
        }

        switch (ktora) {
            case 1001:
                return Double.toString(Math.pow(Double.parseDouble(token1), Double.parseDouble(token2)));
            case 1002:
                return Double.toString(Math.min(Double.parseDouble(token1), Double.parseDouble(token2)));
            case 1003:
                return Double.toString(Math.max(Double.parseDouble(token1), Double.parseDouble(token2)));
            default:
                return "błąd";
        }
    }
    /*
     * -----------------------------------------
     */

    /**
     * Funkcja sortuje dane w przedziale. Wywoływana jest funkcja Array.sort
     * Niestety sposób działania funkcji wymusza na niej by coś zwracała i
     * zwraca 0
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return zwraca 0
     */
    static String sort(Komorka k, ArrayList<String> adresy) {

        double[] wartosci = new double[adresy.size()];
        int i = 0;
        String subform = "";
        for (String s : adresy) {
            subform = k.props.getProperty(s);
            if (subform == null) {
                subform = "0";
            }
            if (isNumber(subform) == false) {
                subform = "0";
            }
            wartosci[i] = Double.parseDouble(subform);
            i++;
        }
        Arrays.sort(wartosci);
        int j = 0;
        for (String s : adresy) {
            k.define(s, Double.toString(wartosci[j]));
            j++;
        }


        return "0";
    }

    /*
     * -----------------------------------------
     */
    /**
     * Funkcja oblicza medianę z przedziału. Dane w przedziale nie muszą być
     * posortowane, funkcja sama to robi nie zmieniając danych.
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return zwraca medianę
     */
    static String mediana(Komorka k, ArrayList<String> adresy) {

        double[] wartosci = new double[adresy.size()];
        int i = 0;
        String subform = "";
        for (String s : adresy) {
            subform = k.props.getProperty(s);
            if (subform == null) {
                subform = "0";
            }
            if (isNumber(subform) == false) {
                subform = "0";
            }
            wartosci[i] = Double.parseDouble(subform);
            i++;
        }
        Arrays.sort(wartosci);
        int ilosc = adresy.size();
        if (ilosc % 2 == 1) {
            return Double.toString(wartosci[ilosc / 2]);
        } else {
            return Double.toString(wartosci[ilosc / 2 + 1]);
        }

    }

    /**
     * Funkcja obliczająca iloczyn z przedziału liczb.
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return ciąg z wynikiem
     */
    static String iloczyn(Komorka k, ArrayList<String> adresy) {

        String subform = "";
        double iloczyn = 1;
        for (String s : adresy) {
            subform = k.props.getProperty(s);
            if (subform == null) {
                subform = "0";
            }
            if (isNumber(subform) == false) {
                subform = "0";
            }
            iloczyn *= Double.parseDouble(subform);

        }
        return Double.toString(iloczyn);

    }

    /**
     * Funkcja obliczająca sumę na przedziale.
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return ciąg z wynikiem
     */
    static String suma(Komorka k, ArrayList<String> adresy) {

        String subform = "";
        double suma = 0;
        for (String s : adresy) {
            subform = k.props.getProperty(s);
            if (subform == null) {
                subform = "0";
            }
            if (isNumber(subform) == false) {
                subform = "0";
            }
            suma += Double.parseDouble(subform);

        }
        return Double.toString(suma);

    }

    /**
     * Funkcja obliczająca różnicę w przedziale.
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return ciąg z wynikiem
     */
    static String roznica(Komorka k, ArrayList<String> adresy) {

        String subform = "";
        double roznica = 0;
        for (String s : adresy) {
            subform = k.props.getProperty(s);
            if (subform == null) {
                subform = "0";
            }
            if (isNumber(subform) == false) {
                subform = "0";
            }
            roznica -= Double.parseDouble(subform);

        }
        return Double.toString(roznica);

    }

    /**
     * Funkcja zwracająca średnią wartość danych w przedziale. funkcja oblicza
     * sume danych po czym dzieli ją, przez ich ilość
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return ciąg z wynikiem
     */
    static String srednia(Komorka k, ArrayList<String> adresy) {

        double sum = 0, ilosc = 0;

        sum = Double.parseDouble(suma(k, adresy));
        ilosc = adresy.size();
        return Double.toString(sum / ilosc);
    }

    /**
     * Zwraca wartość największą w przedziale
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return wynik Math.max
     */
    static String max(Komorka k, ArrayList<String> adresy) {

        String subform = "";
        String l1 = k.props.getProperty(adresy.get(0));
        if (isNumber(l1) == false) {
            l1 = "-999999";
        }
        String l2 = "";
        for (String s : adresy) {

            subform = k.props.getProperty(s);
            if (subform == null) {
                subform = "0";
            }
            if (isNumber(subform) == false) {
                subform = "-999999";
            }
            l2 = subform;

            l1 = Double.toString(Math.max(Double.parseDouble(l1), Double.parseDouble(l2)));
        }
        return l1;
    }

    /**
     * Funkcja zwraca wartość najmniejszą z podanego przedziału.
     *
     * @param k Arkusz w którym znajdują się komórki
     * @param adresy lista z adresami
     * @return wynik Math.min
     */
    static String min(Komorka k, ArrayList<String> adresy) {

        String subform = "";
        String l1 = k.props.getProperty(adresy.get(0));
        if (isNumber(l1) == false) {
            l1 = "999999";
        }
        String l2 = "";
        for (String s : adresy) {

            subform = k.props.getProperty(s);
            if (subform == null) {
                subform = "0";
            }
            if (isNumber(subform) == false) {
                subform = "999999";
            }
            l2 = subform;

            l1 = Double.toString(Math.min(Double.parseDouble(l1), Double.parseDouble(l2)));
        }
        return l1;
    }
}
