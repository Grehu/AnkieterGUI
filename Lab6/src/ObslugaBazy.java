/**@author Grzegorz M�ka*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**Klasa ta s�u�y do ��czenia si� z baz� danych i wykonywania na niej operacji*/
public class ObslugaBazy {
	
	/** Nazwa u�ytkownika i has�o do bazy. Konieczne, aby po��czenie zadzia�a�o.*/
	static String nazwaUzytkownika = "root";
	static String haslo = "";
	
	
	/** Odno�niki do sterownik�w*/
	private static String driver = "com.mysql.jdbc.Driver";
	//private static String url = "jdbc:mysql://127.0.0.1:3306/ankiety";
	
	/**Konstruktor raz, a porz�dnie �aduj�cy sterownik*/
	ObslugaBazy (){
		if (ladujSterownik())
			System.out.println("Za�adowano sterownik.");
		else
		{
			System.err.println("Z powodu b��du �adowania sterownika program zostanie zamkni�ty.");
			System.exit(1);
		}
			
	}
	
	/**
	 * Metoda �aduje sterownik jdbc
	 * 
	 * @return true/false
	 */
	public static boolean ladujSterownik() {
		try {
			Class.forName(driver).newInstance();
			return true;
		} catch (Exception e) {
			System.out.println("Blad przy ladowaniu sterownika bazy!");
			return false;
		}
	}
	
	/**
	* Metoda s�u�y do nawi�zania po��czenia z baz� danych
	* 
	* @param adress
	*            - adres bazy danych
	* @param dataBaseName
	*            - nazwa bazy
	* @param userName
	*            - login do bazy
	* @param password
	*            - has�o do bazy
	* @return - po��czenie z baz�
	*/
	public static Connection connectToDatabase(String adress,
			String dataBaseName, String userName, String password) {
		String baza = "jdbc:mysql://" + adress + "/" + dataBaseName;
		// objasnienie opisu bazy:
		// jdbc: - mechanizm laczenia z baza (moze byc inny, np. odbc)
		// mysql: - rodzaj bazy
		// adress - adres serwera z baza (moze byc tez w nazwy)
		// dataBaseName - nazwa bazy 
		java.sql.Connection connection = null;
		try {
			connection = DriverManager.getConnection(baza, userName, password);
		} catch (SQLException e) {
			System.err.println("Blad przy ��czeniu z baz�");
			System.out.println(e.getMessage());
		}
		return connection;
	}
	
	/**
	 * Metoda s�u�y do po��czenia z MySQL bez wybierania konkretnej bazy
	 * 
	 * @return referencja do uchwytu bazy danych
	 */
	public static Connection getConnection(String adres, int port) {
 
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", nazwaUzytkownika);
		connectionProps.put("password", haslo);
 
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + adres + ":" + port + "/",
					connectionProps);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	

	/**
	 * Wykonanie kwerendy i przes�anie wynik�w do obiektu ResultSet
	 * 
	 * @param s
	 *            - Statement
	 * @param sql
	 *            - zapytanie
	 * @return wynik
	 */
	public static ResultSet executeQuery(Statement s, String sql) {
		try {
			return s.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 * Tworzenie obiektu Statement przesy�aj�cego zapytania do bazy connection
	 * 
	 * @param connection - po��czenie z baz�
	 * @return obiekt Statement przesy�aj�cy zapytania do bazy
	 */
	public static Statement createStatement(Connection connection) {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
 
	/**
	 * Zamykanie po��czenia z baz� danych
	 * 
	 * @param connection - po��czenie z baz�
	 * @param s - obiekt przesy�aj�cy zapytanie do bazy
	 */
	public static void closeConnection(Connection connection, Statement s) {
		try {
			s.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("Bl�d przy zamykaniu pol�czenia " + e.toString());
			System.exit(4);
		}
	}
	
	
	/**Metoda tworz�ca baz� danych w przypadku, gdy nie zosta�a ona jeszcze utworzona.
	 * @param nazwa
	 * 				- nazwa bazy danych
	 * @param listaKomend
	 * 				- lista �a�cuch�w znakowych z poleceniami SQL modyfikuj�cymi
	 * 				  baz� danych
	 * */
	public void stworzBaze(String nazwa, ArrayList<String> listaKomend){
		try{
			System.out.println(" Baza danych '"+nazwa+"' nie istnieje i zostanie teraz utworzona.");
			java.sql.Connection connection = getConnection ("127.0.0.1", 3306);
			Statement s = createStatement(connection);
			executeUpdate(s, "CREATE database "+nazwa);
			closeConnection(connection, s);
			connection = connectToDatabase ("127.0.0.1",
					nazwa, nazwaUzytkownika, haslo);
			s = createStatement(connection);
			for (int i=0; i<listaKomend.size();i++)
				executeUpdate(s, listaKomend.get(i));
			closeConnection(connection, s);
		}
		catch (Exception e){
			System.out.println("B��d tworzenia bazy danych");
		}
	}
	/**Metoda modyfikuj�ca baz� danych
	 *  @param s  - Statement
	 * @param sql - zapytanie
	 */
	private static int executeUpdate(Statement s, String sql) {
		try {
			return s.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	/** Metoda pobieraj�ca pytanie o okre�lonym numerze z bazy.
	 * 
	 * @param numer - numer pytania, kt�re chcemy pobra�
	 * @return pytanie do zapisania w postaci obiektu
	 * @throws SQLException
	 */
	public Pytanie pobierzZBazy(int numer) throws SQLException{
		try{
			java.sql.Connection connection = connectToDatabase("127.0.0.1",
					"ankiety", nazwaUzytkownika, haslo);
	 
			// WYKONYWANIE OPERACJI NA BAZIE DANYCH
			String sql = "Select * from pytania where ID_pytania="+Integer.toString(numer)+" ORDER BY ID_elementu;" ;
			Statement s = createStatement(connection);
			ResultSet r = executeQuery(s, sql);
			Pytanie pytanie = new Pytanie ("", "");
			while (r.next()){
				if (r.getInt("ID_elementu")==0)
					pytanie = new Pytanie (r.getString("tresc"), r.getString("typ"));
				else{
					pytanie.dodajOdpowiedz(r.getString("tresc"));
				}
			}
			closeConnection(connection, s);
			return pytanie;
		}
		catch (SQLException e){
			System.out.println("B��d pobrania pytania z bazy.");
			Pytanie brak = new Pytanie("Brak tresci pytania.","pytanie");
			return brak;
		}
	}
	
	/** Metoda zapisuj�ca odpowiedzi u�ytkownika w bazie danych
	 * @param listaKomend - lista polece� SQL zapisuj�cych odpowiedzi w bazie danych
	 * @throws SQLException
	 * */
	public void zapiszPrzygotowaneOdpowiedzi(ArrayList<String> listaKomend){
		try{
			Connection connection = connectToDatabase ("127.0.0.1",
					"ankiety", nazwaUzytkownika, haslo);
			Statement s = createStatement(connection);
			for (int i=0; i<listaKomend.size();i++){
				executeUpdate(s, listaKomend.get(i));
			}
			closeConnection(connection, s);
		}
		catch (Exception e){
			System.out.println("B��d dodawania odpowiedzi");
		}
	}
	
	/** Metoda pobieraj�ca z bazy numer ostatniego pytania w celu oznaczenia go jako ostatnie
	 * 
	 * @return numery ostatniego pytania/ -1 gdy nie mo�na po��czy� si� z baz�
	 */
	public int numerOstatniegoPytania(){
		int numer=0;
		try{
			java.sql.Connection connection = connectToDatabase("127.0.0.1",
					"ankiety", nazwaUzytkownika, haslo);
				// WYKONYWANIE OPERACJI NA BAZIE DANYCH
				String sql = "SELECT MAX(ID_PYTANIA) FROM PYTANIA;" ;
				Statement s = createStatement(connection);
				ResultSet r = executeQuery(s, sql);
				r.next();
				numer=r.getInt("MAX(ID_PYTANIA)");
				closeConnection(connection, s);
		}
		catch (SQLException e){
			System.err.println("B��d pobierania numeru ostatniego pytania");
			e.printStackTrace();
			numer=-1;
		}
		catch (NullPointerException e){
			System.err.println("B��d pobierania numeru ostatniego pytania - Je�li baza nie istnieje, zostanie teraz utworzona.");
			e.printStackTrace();
			numer=-1;
		}
		
		return numer;
	}
	
	/** Metoda pobieraj�ca z bazy ID ostatniego zapisanego klienta w 
	 * celu rozpocz�cia nadawania numer�w nowym klientom od numeru jeszcze nie istniej�cego
	 * 
	 * @return numer ostatniego klienta lub 0, gdy baza nie istnieje/nie mo�na si� z ni� po��czy�
	 */
	public int numerOstatniegoKlienta(){
		int numer=0;
		try{
			java.sql.Connection connection = connectToDatabase("127.0.0.1",
					"ankiety", nazwaUzytkownika, haslo);
				String sql = "SELECT MAX(ID_uzytkownika) FROM odpowiedzi;" ;
				Statement s = createStatement(connection);
				ResultSet r = executeQuery(s, sql);
				r.next();
				numer=r.getInt("MAX(ID_uzytkownika)");
				closeConnection(connection, s);
		}
		catch (Exception e){
			System.err.println("B��d pobierania numeru ostatniego uzytkownika. Prawdopodonie nie ma �adnej zapisanej odpowiedzi.");
		}
		return numer;
	}
	
	/**Uniwersalna metoda wysy�aj�ca do bazy zapytanie i zwracaj�ca wynik
	 * 
	 * @param zapytanie - dowolne zapytanie w MySQL
	 * @return Wynik zapytania
	 */
	public ResultSet pobierzInne (String zapytanie){
		try{
			java.sql.Connection connection = connectToDatabase("127.0.0.1",
					"ankiety", nazwaUzytkownika, haslo);
				Statement s = createStatement(connection);
				ResultSet r = executeQuery(s, zapytanie);
				//closeConnection(connection, s);
				return r;
		}
		catch (Exception e){
			System.err.println("B��d wykonania zapytania.");
			return null;
		}
	}
}