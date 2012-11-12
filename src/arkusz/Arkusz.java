/**
 * Arkusz.java
 */
package arkusz;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.script.ScriptException;

/**
 *
 * @author Łukasz Świderski i Karol Pawluczuk
 */
public class Arkusz extends JFrame {

    /**
     * Klasa będąca implementacją TableCellRender.
     */
    class Naglowki extends JLabel implements TableCellRenderer {

        /**
         * Wyświetla ramkę okna, Nagłówki kolumn i wierszy. Ustawia kolor
         * nagłówków na szary w przeciwieństwie do białych komórek
         *
         * @param table tabela
         */
        Naglowki(JTable table) {
            JTableHeader header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setBackground(header.getBackground());
            setForeground(header.getForeground());

            setFont(header.getFont());
        }

        /**
         * Tworzy nazwy wierszy. Zgodnie z zasadą implementacji metoda musi być
         * nadpisana więc pomimo tak dużej liczby parametrów wykorzystywany jest
         * tylko jeden z ilością wiersz.
         *
         *
         * @param row
         *
         * @return
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setText((row + 1) + "");
            return this;
        }
    }

    /*
     * ----------------------------------------
     */
    /**
     * Klasa dziedzicząca z DefaultTableModel
     */
    class OknoTabeli extends DefaultTableModel {

        Komorka komorka = new Komorka();

        /**
         * Tworzy tabele w stylu arkusza o wymiarach _wiersze x _kolumny
         *
         * @param _wiersze ilość wierszy
         * @param _kolumny ilość kolumn
         */
        public OknoTabeli(int _wiersze, int _kolumny) {
            super(_wiersze, _kolumny);
        }

        /**
         * metoda pozyskuje obiekt z współrzędnych _wiersz i _kolumna
         *
         * @param _wiersz współrzędna y
         * @param _kolumna współrzędna x
         * @return zwraca obiekt z tych współrzędnych
         */
        public Object getValueAt(int _wiersz, int _kolumna) {
            try {
                return komorka.getValue(getKey(_wiersz, _kolumna));
            } catch (ScriptException ex) {
                return new Object();
            }
        }

        /**
         * Funkcja zwraca formułę/wartość znjadującą się pod konkretnym adresem
         *
         * @param _wiersz
         * @param _kolumna
         * @return zwraca String kryjący się pod komórką
         */
        public Object getFormula(int _wiersz, int _kolumna) {
            return komorka.getFormula(getKey(_wiersz, _kolumna));
        }

        /**
         * Ustawia wybrany obiekt w odpowiedniej komórce. Po czym "maluje"
         * tabele na nowo
         *
         * @param value obiekt do dodania
         * @param _wiersz y
         * @param _kolumna x
         */
        public void setValueAt(Object value, int _wiersz, int _kolumna) {
            komorka.define(getKey(_wiersz, _kolumna), value.toString());
            table.repaint();
        }

        /**
         * Czyści tabelę. usuwan wszystkie weirsze po czym tworzy nowe
         */
        public void reset() {
            komorka.clear();
            okno.setRowCount(0);
            okno.setRowCount(ROW_COUNT);
        }

        /**
         * Zwraca Properties. Taka tabela wywodząca się z klasy Hashtable
         *
         * @return properties
         */
        public Properties getProperties() {
            return komorka.getProperties();
        }

        /**
         * Zamienia adres z postaci X x Y na postać alfanumeryczną w stylu A1
         *
         * @param _wiersz y
         * @param column x
         * @return String z adresem alfanumerycznym
         */
        String getKey(int _wiersz, int column) {
            return Character.toString((char) (column + 65)) + "" + (_wiersz + 1);
        }
    }
    /*
     * ----------------------------------------
     */
    /**
     * liczba wierszy.
     */
    final int ROW_COUNT = 50;
    /**
     * liczba kolumn.
     */
    final int COLUMN_COUNT = 25;
    /**
     * Główne okno programu
     */
    JTable table;
    /**
     * Pole do wpisywania formuł.
     */
    JTable pole_formuly;
    /**
     * Tabelki w kształcie arkusza.
     */
    OknoTabeli okno;
    /**
     * pole tekstowe.
     */
    JTextField evalField;
    /**
     * Mapa opisów.
     */
    static Map<String, String> opisy = new HashMap<String, String>();

    /**
     * Kontruktor arkusza. Łączy wszystkie obiekty składające się na GUI w
     * całość. oraz wyświetla Splashscreena przy uruchomieniu
     */
    public Arkusz() {
        super("JCalc 1.0");
        /*
         * --------mapa do pomocy -------
         */
        opisy.put("FSIN", "<html>fsin(parametr)<br><br>zwraca Sinus kąta podanego w stopniach<br><br>Przykład:<br> =fsin(A1)</html>");
        opisy.put("FCOS", "<html>fcos(parametr)<br><br>zwraca Cosinus kąta podanego w stopniach<br><br>Przykład:<br> =fcos(A1)</html>");
        opisy.put("FTG", "<html>ftg(parametr)<br><br>zwraca Tangens kąta podanego w stopniach<br><br>Przykład:<br> =fsin(A1)</html>");
        opisy.put("FCTG", "<html>fctg(parametr)<br><br>zwraca Cotangens kąta podanego w stopniach<br><br>Przykład:<br> =fsin(A1)</html>");
        opisy.put("FASIN", "<html>fasin(parametr)<br><br>zwraca Arcus Sinus kąta podanego w stopniach<br><br>Przykład:<br> =fasin(A1)</html>");
        opisy.put("FACOS", "<html>facos(parametr)<br><br>zwraca Arcus Cosinus kąta podanego w stopniach<br><br>Przykład:<br> =facos(A1)</html>");
        opisy.put("FATG", "<html>fatg(parametr)<br><br>zwraca Arcus Tangens kąta podanego w stopniach<br><br>Przykład:<br> =fatg(A1)</html>");
        opisy.put("FACTG", "<html>factg(parametr)<br><br>zwraca Arcus Cotangens kąta podanego w stopniach<br><br>Przykład:<br> =actg(A1)</html>");

        opisy.put("FSINR", "<html>fsinr(parametr)<br><br>zwraca Sinus kąta podanego w radianach<br><br>Przykład:<br> =fsinr(A1)</html>");
        opisy.put("FCOSR", "<html>fcosr(parametr)<br><br>zwraca Cosinus kąta podanego w radianach<br><br>Przykład:<br> =fcosr(A1)</html>");
        opisy.put("FTGR", "<html>ftgr(parametr)<br><br>zwraca Tangens kąta podanego w radianach<br><br>Przykład:<br> =fsinr(A1)</html>");
        opisy.put("FCTGR", "<html>fctgr(parametr)<br><br>zwraca Cotangens kąta podanego w radianach<br><br>Przykład:<br> =fsinr(A1)</html>");
        opisy.put("FASINR", "<html>fasinr(parametr)<br><br>zwraca Arcus Sinus kąta podanego w radianach<br><br>Przykład:<br> =fasinr(A1)</html>");
        opisy.put("FACOSR", "<html>facosr(parametr)<br><br>zwraca Arcus Cosinus kąta podanego w radianach<br><br>Przykład:<br> =facosr(A1)</html>");
        opisy.put("FATGR", "<html>fatgr(parametr)<br><br>zwraca Arcus Tangens kąta podanego w radianach<br><br>Przykład:<br> =fatgr(A1)</html>");
        opisy.put("FACTGR", "<html>factgr(parametr)<br><br>zwraca Arcus Cotangens kąta podanego w radianach<br><br>Przykład:<br> =actgr(A1)</html>");

        opisy.put("FFAC", "<html>ffac(parametr)<br><br>zwraca Silnia z parametru<br>!parametr<br><br>Przykład:<br> =ffac(5)<br>zwraca 120</html>");
        opisy.put("FABS", "<html>fabs(parametr)<br><br>zwraca Moduł z parametru<br>|parametr|<br><br>Przykład:<br> =fabs(-10)<br>zwraca 10</html>");
        opisy.put("FSQRT", "<html>fsqrt(parametr)<br><br>zwraca Pierwiastek z parametru<br><br>Przykład:<br> =fsqrt(9)<br>zwraca 3</html>");


        opisy.put("FSUM", "<html>fsum(parametr 1 : parametr 2)<br><br>zwraca Sumę z przedziału (parametr 1, parametr 2)<br><br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br>=fsum(A1:D4)</html>");
        opisy.put("FSR", "<html>fsr(parametr 1 : parametr 2)<br><br>zwraca Średnią arytmetyczną z przedziału (parametr 1, parametr 2)<br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br><br>=fsr(A1:D4)</html>");
        opisy.put("FMAX", "<html>fmax(parametr 1 : parametr 2)<br><br>zwraca Największą wartość w przedziale (parametr 1, parametr 2)<br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br><br>=fmax(A1:D4)</html>");
        opisy.put("FMIN", "<html>fmin(parametr 1 : parametr 2)<br><br>zwraca Najmniejszą wartość w przedziale (parametr 1, parametr 2)<br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br><br>=fmin(A1:D4)</html>");
        opisy.put("FMED", "<html>fmed(parametr 1 : parametr 2)<br><br>zwraca Medianę z przedzialu (parametr 1, parametr 2)<br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br><br>=fmed(A1:D4)</html>");
        opisy.put("FILO", "<html>filo(parametr 1 : parametr 2)<br><br>zwraca Iloczyn z przedzialu (parametr 1, parametr 2)<br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br><br>=filo(A1:D4)</html>");
        opisy.put("FROZ", "<html>froz(parametr 1 : parametr 2)<br><br>zwraca Różnicę z przedzialu (parametr 1, parametr 2)<br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br><br>=froz(A1:D4)</html>");
        opisy.put("FSORT", "<html>fsort(parametr 1 : parametr 2)<br><br>Sortuje wartosci w przediale (parametr 1, parametr 2)<br><u>Ważne!</u><br>Parametry muszą być adresami komórek<br>Przykład:<br><br>=fsort(A1:D4)</html>");


        opisy.put("FPOW", "<html>fpow(parametr 1 ; parametr 2)<br><br>Wykonuje potęgowanie <br>parametr 1 do parametr 2<br> Przykład:<br>=fpow(5,2)<br>Zwraca 25</html>");
        opisy.put("FMINL", "<html>fminl(parametr 1 ; parametr 2)<br><br>Porownuje 2 liczby<br>Zwraca Mniejszą z nich<br> Przykład:<br>=fminl(5,2)<br>Zwraca 2</html>");
        opisy.put("FMAXL", "<html>fmaxl(parametr 1 ; parametr 2)<br><br>Porownuje 2 liczby<br>Zwraca Większą z nich<br> Przykład:<br>=fmaxl(5,2)<br>Zwraca 5</html>");


        /*
         * ----koniec mapy do pomocy -----
         */
        makeMenu();
        poleFormuly();
        czysc();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //SplashScreen
        // wymagane uruchomienie z pliku .jar
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
            this.setVisible(true);
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            this.setVisible(true);
            return;
        }
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
            }
        }
        splash.close();
        //koniec Splahscreena

        this.setVisible(true);

    }

    /**
     * Metoda zapisująca. Wykorzystane są tutaj klasy wbudowane w Javę
     * JFileChooser, File i FileOutputStream Za pomocą obiektu klasy Properties
     * dane z każdej komórki są zapisywane w postaci plaintext (czysty text) w
     * pliku textowym
     */
    private void zapisz() {

        JFileChooser fc = new JFileChooser();
        int zawartosc = fc.showSaveDialog(null);

        if (zawartosc == JFileChooser.APPROVE_OPTION) {
            File outFile = fc.getSelectedFile();
            Properties props = okno.getProperties();
            try {
                props.store(new FileOutputStream(outFile), "Arkusz");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Metoda odczytująca z pliku. Podobnie jak metoda zapisująca, wykorzustuje
     * ona klasy wbudowane w Javę JFileChooser, File i FileInputStream co
     * pozwala nam w łatwy sposób wczytać pliki.
     */
    private void otworzPlik() {
        JFileChooser fc = new JFileChooser();
        int zawartosc = fc.showOpenDialog(null); // jesli zero znaczy OK

        if (zawartosc == JFileChooser.APPROVE_OPTION) {
            czysc();
            File inFile = fc.getSelectedFile();
            try {
                Properties props = okno.getProperties();
                props.load(new FileInputStream(inFile));

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Metoda Tworzy wyskakujące okienko z informacjami o autorach. Za pomocą
     * obiektu klasy JDialog Wyświetlani są autorzy projektu LS i KP
     */
    public void autorzy() {

        JDialog wyskocz = new JDialog(this, "Autorzy", true);

        wyskocz.setResizable(false);


        wyskocz.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel p = new JPanel();
        JLabel jLabel1 = new javax.swing.JLabel();
        JLabel jLabel2 = new javax.swing.JLabel();
        JLabel jLabel3 = new javax.swing.JLabel();
        JLabel jLabel4 = new javax.swing.JLabel();

        jLabel1.setText("Autorzy:");

        jLabel2.setText("1. Łukasz Świderski");

        jLabel3.setText("2. Karol Pawluczuk");

        jLabel4.setText("PS6 2012");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(p);
        p.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(51, 51, 51).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel1).addComponent(jLabel2).addComponent(jLabel3))).addGroup(layout.createSequentialGroup().addGap(74, 74, 74).addComponent(jLabel4))).addContainerGap(65, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(32, 32, 32).addComponent(jLabel1).addGap(18, 18, 18).addComponent(jLabel2).addGap(18, 18, 18).addComponent(jLabel3).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE).addComponent(jLabel4).addContainerGap()));
        wyskocz.add(p);
        wyskocz.pack();

        wysrodkujOkienko(wyskocz);

        wyskocz.setVisible(true);

    }

    /**
     * Metoda tworzy okienko pomocy. Wykorzystana jest tu klasa JTabbedPane
     * przedstawiająca zakładki oraz zwykłe JLabely z tekstem. W opisie funkcji
     * wykorzysta zstała lista JList i powiązana z nią mapa z opisem funkcji
     */
    public void pomoc() {

        JDialog wyskocz = new JDialog(this, "Pomoc", true);

        wyskocz.setResizable(false);


        wyskocz.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel p;
        p = new JPanel();
        JLabel jLabel2 = new javax.swing.JLabel();
        JLabel jLabel3 = new javax.swing.JLabel();
        JLabel jLabel1 = new javax.swing.JLabel();
        JLabel jLabel4 = new javax.swing.JLabel();
        JLabel jLabel5 = new javax.swing.JLabel();
        JLabel jLabel6 = new javax.swing.JLabel();
        JLabel jLabel7 = new javax.swing.JLabel();
        JLabel jLabel8 = new javax.swing.JLabel();
        JLabel jLabel9 = new javax.swing.JLabel();
        JLabel jLabel10 = new javax.swing.JLabel();
        JLabel jLabel11 = new javax.swing.JLabel();
        JLabel jLabel12 = new javax.swing.JLabel();
        JLabel jLabel13 = new javax.swing.JLabel();

        jLabel2.setText("Wyrażenie rozpoczynamy znakiem \"=\"");

        jLabel3.setText("W wyrażeniu można używać:");
        jLabel3.setAlignmentY(1.0F);

        jLabel1.setText("-liczby (np. 5)");

        jLabel4.setText("-Adresu Komorki (np. D3)");

        jLabel5.setText("-Funkcji z parametrem (np. fsin(90))");

        jLabel6.setText("Wyróżniamy 3 rodzaje funkcji:");

        jLabel7.setText("-dwu - parametrowe: 2 parametry (liczba lub adres) oddzielone średnikiem ;");

        jLabel8.setText("-zakresowe: 2 parametry (adres) opisujące zakres oddzielone dwukropkiem :");

        jLabel9.setText("-1 lub wieloparametrowe: jako parametry przyjmują one liczby, adresy,");

        jLabel10.setText("inne funkcje. Parametry oddzielone są znakami arytmetycznymi + - / *");

        jLabel11.setText("Przykład:");

        jLabel12.setText("=fsin(A1)+fcos(B2)");

        jLabel13.setText("=fsin(fcos(90+B5))");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(p);
        p.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel6).addComponent(jLabel11).addGroup(layout.createSequentialGroup().addGap(10, 10, 10).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel7).addComponent(jLabel4).addComponent(jLabel1).addComponent(jLabel5).addComponent(jLabel8).addComponent(jLabel9).addGroup(layout.createSequentialGroup().addGap(10, 10, 10).addComponent(jLabel10)).addComponent(jLabel12).addComponent(jLabel13)))).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel4).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel5).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel7).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel8).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel9).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel10).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel11).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel12).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel13).addContainerGap(50, Short.MAX_VALUE)));


        tabbedPane.addTab("Składnia", new ImageIcon(), p, "Jak budować wyrażenia");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);


        p = new JPanel();

        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        JButton jButton1 = new javax.swing.JButton();
        final JList jList1 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();

        final JLabel tresc = new JLabel("Wybierz z listy obok");



        jList1.setModel(new javax.swing.AbstractListModel() {

            String[] strings = {"FSIN",
                "FCOS",
                "FTG",
                "FCTG",
                "FASIN",
                "FACOS",
                "FATG",
                "FACTG",
                "FSINR",
                "FCOSR",
                "FTGR",
                "FCTGR",
                "FASINR",
                "FACOSR",
                "FATGR",
                "FACTGR",
                "FFAC",
                "FABS",
                "FSQRT",
                "FSUM",
                "FSR",
                "FMAX",
                "FMIN",
                "FMED",
                "FILO",
                "FROZ",
                "FSORT",
                "FPOW",
                "FMINL",
                "FMAXL"};

            /**
             * @return zwraca wielkość tablicy skrótów funkcji
             */
            public int getSize() {
                return strings.length;
            }

            /**
             * @return zwraca obiekt z tablicy skrótów funkcji
             */
            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel1.setText("Opis:");

        jButton1.setText("Pokaż");

        jButton1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {

                tresc.setText(opisy.get(jList1.getSelectedValue()));


            }
        });

        layout = new javax.swing.GroupLayout(p);
        p.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(29, 29, 29).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(34, 34, 34).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel1).addComponent(jButton1).addComponent(tresc, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(24, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(26, 26, 26).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(tresc, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(40, 40, 40).addComponent(jButton1)).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(23, Short.MAX_VALUE)));

        tabbedPane.addTab("Funkcje", new ImageIcon(),
                p, "Spis funkcji");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        wyskocz.add(tabbedPane);



        wyskocz.pack();

        wysrodkujOkienko(wyskocz);

        wyskocz.setVisible(true);

    }

    /**
     * Wyśrodkowuje Dialogi. Pozyskuje rozdzielczość komputera i na podstawie
     * otrzymanych wartośći ustawia wyskakujące okna na środku ekranu
     *
     * @param okienko Dialog który ma być wyśrodkowany
     */
    public void wysrodkujOkienko(JDialog okienko) {

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        int w = okienko.getSize().width;
        int h = okienko.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;


        okienko.setLocation(x, y);
    }

    /**
     * Funkcja wywołująca restetowanie okna.
     */
    private void czysc() {
        okno.reset();

    }

    /**
     * Dodaje wybraną opcję do paska menu.
     *
     * @param menu menu do którego ma dodać
     * @param tytul nazwa opcji
     * @param actionListener co ma wydarzyć się po wybraniu opcji
     */
    private void addMenuItem(JMenu menu, String tytul, ActionListener actionListener) {
        JMenuItem item = new JMenuItem(tytul);
        item.setText(tytul);
        item.addActionListener(actionListener);
        menu.add(item);
    }

    /**
     * Tworzy pasek menu, menu obrazkowe oraz ostateczne okno programu. Łączy
     * część edytowalną przez urzytkownika w całość or ustawia właściwości
     */
    private void makeMenu() {


        JPanel menu = new JPanel();



        ImageIcon save = new ImageIcon(getClass().getResource("images/save.png"));
        ImageIcon open = new ImageIcon(getClass().getResource("images/open.png"));
        ImageIcon nowy = new ImageIcon(getClass().getResource("images/new.png"));
        ImageIcon pomoc = new ImageIcon(getClass().getResource("images/pomoc.png"));
        ImageIcon au = new ImageIcon(getClass().getResource("images/au.png"));
        JButton bsave = new JButton(save);
        bsave.setPreferredSize(new Dimension(22, 22));


        bsave.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                zapisz();
            }
        });
        bsave.setToolTipText("Zapisz");

        JButton bopen = new JButton(open);
        bopen.setPreferredSize(new Dimension(22, 22));
        bopen.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                otworzPlik();
            }
        });
        bopen.setToolTipText("Otworz");

        JButton bnowy = new JButton(nowy);
        bnowy.setPreferredSize(new Dimension(22, 22));
        bnowy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                czysc();
            }
        });
        bnowy.setToolTipText("Nowy Arkusz");

        JButton bpomoc = new JButton(pomoc);
        bpomoc.setPreferredSize(new Dimension(22, 22));
        bpomoc.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                pomoc();
            }
        });
        bpomoc.setToolTipText("Pomoc");

        JButton bau = new JButton(au);
        bau.setPreferredSize(new Dimension(22, 22));
        bau.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                autorzy();
            }
        });
        bau.setToolTipText("Autorzy");

        menu.setLayout(new FlowLayout(FlowLayout.LEFT));
        menu.add(bnowy);
        menu.add(bsave);
        menu.add(bopen);
        menu.add(bpomoc);
        menu.add(bau);



        JMenuBar bar = new JMenuBar();
        JMenu menu1 = new JMenu("Plik");
        menu1.addSeparator();
        addMenuItem(menu1, "Nowy Arkusz", new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                czysc();
            }
        });
        menu1.addSeparator();
        addMenuItem(menu1, "Zapisz", new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                zapisz();
            }
        });
        addMenuItem(menu1, "Otworz", new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                otworzPlik();
            }
        });
        menu1.addSeparator();

        addMenuItem(menu1, "Koniec", new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        bar.add(menu1);

        JMenu menu2 = new JMenu("About");
        addMenuItem(menu2, "Pomoc", new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                pomoc();
            }
        });
        addMenuItem(menu2, "Autorzy", new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                autorzy();
            }
        });
        bar.add(menu2);

        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //pozwala rozszerzy kolumny do naturalnych rozmiarow
        table.setRowSelectionAllowed(true);         //naturalne zaznaczanie
        table.setColumnSelectionAllowed(true);      // -----||------
        okno = new OknoTabeli(ROW_COUNT, COLUMN_COUNT);

        table.setModel(okno);

        pole_formuly = new JTable();
        pole_formuly.setModel(new DefaultTableModel(ROW_COUNT, 1));
        LookAndFeel.installColorsAndFont(pole_formuly, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
        pole_formuly.setIntercellSpacing(new Dimension(0, 0));
        Dimension d = pole_formuly.getPreferredScrollableViewportSize();
        d.width = pole_formuly.getPreferredSize().width;
        pole_formuly.setPreferredScrollableViewportSize(d);
        pole_formuly.setRowHeight(table.getRowHeight());
        pole_formuly.setDefaultRenderer(Object.class, new Naglowki(table));

        JScrollPane scrollP = new JScrollPane(table);
        scrollP.setRowHeaderView(pole_formuly);
        add(scrollP, BorderLayout.CENTER);

        JPanel editP = new JPanel();
        editP.setLayout(new FlowLayout(FlowLayout.LEFT));
        editP.add(new JLabel("Formula="));


        evalField = new JTextField();
        evalField.setColumns(50);
        editP.add(evalField);
        JPanel naglowek = new JPanel();
        naglowek.setLayout(new GridLayout(2, 1));
        naglowek.add(menu);
        naglowek.add(editP);

        setJMenuBar(bar);
        add(naglowek, BorderLayout.NORTH);


        pack();
        this.setLocationRelativeTo(null);
    }

    /**
     * Integracja Pola do wpisywania formuł z komórkami.
     */
    private void poleFormuly() {

        ListSelectionListener listener = new ListSelectionListener() {

            /**
             * W polu formuły pokazuje co znajduje się w zaznaczonej komórce.
             */
            public void valueChanged(ListSelectionEvent e) {
                int col = table.getSelectedColumn();
                int row = table.getSelectedRow();
                if (col < 0 || row < 0) {
                    return;
                }
                evalField.setText(okno.getFormula(row, col) + "");

            }
        };

        table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        table.getSelectionModel().addListSelectionListener(listener);

        evalField.addKeyListener(new KeyAdapter() {

            /**
             * Przenosi zawartość pola formuły do zaznaczonej komórki.
             */
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    int col = table.getSelectedColumn();
                    int row = table.getSelectedRow();
                    if (col < 0 || row < 0) {
                        return;
                    }
                    okno.setValueAt(evalField.getText(), row, col);
                }
            }
        });

    }

    public static void main(String[] args) {
        new Arkusz();
    }
}
