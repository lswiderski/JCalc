//Komorka.java
package arkusz;

/**
 *
 * @author Łukasz Świderski i Karol Pawluczuk
 */
import java.util.*;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Komorka {

    final static ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    Properties props = new Properties();
    Map<String, Integer> funkcje = new HashMap<String, Integer>();

    /**
     * funkcja sprawdza czy podany String jest liczbą
     *
     * @param token String do sprawdzenia
     * @return zwraca true jeśli jest liczbą / false gdy nią nie jest
     */
    public boolean isNumber(final String token) {
        try {
            Double.valueOf(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Konstruktor domyślny
     */
    public Komorka() {
        //funkcje 1 argumentowe costam(A1)
        //trygonometryczne w stopniach
        funkcje.put("FSIN", 1);
        funkcje.put("FCOS", 2);
        funkcje.put("FTG", 3);
        funkcje.put("FCTG", 4);
        funkcje.put("FASIN", 5);
        funkcje.put("FACOS", 6);
        funkcje.put("FATG", 7);
        funkcje.put("FACTG", 8);
        //trygonomiczne w radianach 
        funkcje.put("FSINR", 9);
        funkcje.put("FCOSR", 10);
        funkcje.put("FTGR", 11);
        funkcje.put("FCTGR", 12);
        funkcje.put("FASINR", 13);
        funkcje.put("FACOSR", 14);
        funkcje.put("FATGR", 15);
        funkcje.put("FACTGR", 16);

        funkcje.put("FFAC", 17); //silnia factorial 
        funkcje.put("FABS", 18); // modul 
        funkcje.put("FSQRT", 19); // pierwiastek

        //zakresowe costam(A1:B1)
        funkcje.put("FSUM", 101);  //suma 
        funkcje.put("FSR", 102);	 //srednia arytmetyczna
        funkcje.put("FMAX", 103); //max w przedziale
        funkcje.put("FMIN", 104); //min w przedziale
        funkcje.put("FMED", 105); //Mediana  
        funkcje.put("FILO", 106); //iloczyn 
        funkcje.put("FROZ", 107); //roznica 
        funkcje.put("FSORT", 108); //sortowanie 

        //2argumentowe costam (A1;B1) 
        funkcje.put("FPOW", 1001); //potega 
        funkcje.put("FMINL", 1002); // zwraca mniejszą liczbę 
        funkcje.put("FMAXL", 1003); //	zwraca większą liczbę	
    }

    /**
     * funkcja przetwarza formułę wpisaną w komórkę. dzieli funkcje na tzw.
     * Tokeny, czyli krótkie stringi które w orginale są oddzielone znakami
     * arytmetycznymi. Zamienia adresy komórek na wartości jakie się w nich
     * znajdują. Jeśli wykryje funkcję przekierowuje podciąg do odpowiedniej
     * funkcji by ją wykonać. Gdy tokeny będą już samymi liczbami używany jest
     * silnik JavaScript, który potrafi obliczać wyrażenia arytmetyczne
     *
     * @param expr formuła w komórce
     * @return zwraca wynik formuły lub wejściowy String jeśli nie jest formułą
     */
    public String eval(String expr) throws ScriptException {

        if (expr == null) {
            expr = "";
        }
        if (expr.startsWith("=") || expr.startsWith("F") || expr.startsWith("f")) {
            String form = expr.toUpperCase();
            if (form.indexOf('=') > -1) {
                StringBuffer usuwanie = new StringBuffer(form);
                usuwanie.deleteCharAt(form.indexOf('='));
                form = usuwanie.toString();
            }

            StringTokenizer st = new StringTokenizer(form, " =+-*/()");

            String token;


            while (st.hasMoreTokens()) {
                token = st.nextToken();


                if (isNumber(token)) {
                    continue;
                }

                String subform = props.getProperty(token);
                if (subform == null) {
                    subform = "";
                }
                if (sprawdz(token)) {

                    int start = form.indexOf(token) + token.length();
                    int lnc = 0;
                    int ln = 0;
                    for (int i = 0; i < form.length(); i++) {

                        if ('(' == form.charAt(i)) {
                            ln++;

                        }
                        if (')' == form.charAt(i)) {
                            ln--;

                            if (ln == 0) {
                                lnc = i;
                                break;
                            }

                        }

                    }
                    int end = form.indexOf(")", lnc);


                    if (end > -1) {
                        String dousuniecia = form.substring(start + 1, end);

                        subform = wykonaj(token, dousuniecia);

                        StringBuffer nform = new StringBuffer(form);
                        nform = nform.delete(start, end + 1);

                        form = nform.toString();
                    }

                }

                subform = eval(subform);

                form = form.replaceFirst(token, subform);

            }


            try {
                return engine.eval(form) + "";
            } catch (ScriptException e) {
                return "błąd!";
            }

        } else {
            return expr;
        }
    }

    /**
     * Funkcja sprawdza czy mamy doczynienia z funkcją matematyczną
     *
     * @param kandydat potencjalna nazwa funkcji
     * @return true jeśli znajdzie funkcję w mapie / false jeśli jej nie
     * znajdzie
     */
    public boolean sprawdz(String kandydat) {
        return funkcje.containsKey(kandydat);
    }

    /**
     * Funkcja na zlecenie funkcji eval przekierowuje ciąg do odpowiednich
     * kategorii funkcji
     *
     * @param f wykryta funkcja
     * @param ciag parametr funkcji
     * @return Zwraca wynik obliczenia funkcji
     * @throws ScriptException
     */
    public String wykonaj(String f, String ciag) throws ScriptException {

        int funkcja = funkcje.get(f);
        if (funkcja > 0 && funkcja < 100) {
            return Funkcja.fMatProsta(this, ciag, funkcja);
        } else if (funkcja > 99 && funkcja < 1000) {
            return Funkcja.fzakresowa(this, ciag, funkcja);
        } else {
            return Funkcja.dwuArgumentowa(this, ciag, funkcja);
        }
    }

    /**
     * Metoda zwraca właściwości komórek
     *
     * @return właściwości komórek
     */
    public Properties getProperties() {
        return props;
    }

    /**
     * Metoda zapisuje wprowadzoną wartość do konkretnej komórki
     *
     * @param key adres komórki
     * @param val wartość do zapisania
     */
    public void define(String key, String val) {
        props.setProperty(key.toUpperCase(), val);
    }

    /**
     * Metoda zwraca formułę/wartość znjadującą się pod konkretnym adresem
     *
     * @param key adres komórki
     * @return zwraca String kryjący się pod komórką
     */
    public String getFormula(String key) {
        return props.getProperty(key.toUpperCase());
    }

    /**
     * Metoda oblicza wartość formuły
     *
     * @param key adres komórki
     * @return zwraca wynik funkcji eval
     * @throws ScriptException
     */
    public String getValue(String key) throws ScriptException {
        return eval(props.getProperty(key.toUpperCase()));
    }

    /**
     * funkcja czyści ekran
     */
    public void clear() {
        props.clear();
    }

    /**
     * Funkcja sprawdza czy podany adres jest w rzeczywistości adresem komórki
     *
     * @param kandydat rzekomy adres komórki
     * @return zwraca true jeśli jest String jest adresem / False jeśli nie
     */
    public boolean sprawdzAdres(String kandydat) {
        return props.containsKey(kandydat);
    }
}
