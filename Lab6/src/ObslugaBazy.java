/**@author Grzegorz M¹ka*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**Klasa ta s³u¿y do ³¹czenia siê z baz¹ danych i wykonywania na niej operacji*/
public class ObslugaBazy {
	
	/** Nazwa u¿ytkownika i has³o do bazy. Konieczne, aby po³¹czenie zadzia³a³o.*/
	static String nazwaUzytkownika = "root";
	static String haslo = "";
	
	
	/** Odnoœniki do sterowników*/
	private static String driver = "com.mysql.jdbc.Driver";
	//private static String url = "jdbc:mysql://127.0.0.1:3306/ankiety";
	
	/**Konstruktor raz, a porz¹dnie ³aduj¹cy sterownik*/
	ObslugaBazy (){
		if (ladujSterownik())
			System.out.println("Za³adowano sterownik.");
		else
		{
			System.err.println("Z powodu b³êdu ³adowania sterownika program zostanie zamkniêty.");
			System.exit(1);
		}
			
	}
	
	/**
	 * Metoda ³aduje sterownik jdbc
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
	* Metoda s³u¿y do nawi¹zania po³¹czenia z baz¹ danych
	* 
	* @param adress
	*            - adres bazy danych
	* @param dataBaseName
	*            - nazwa bazy
	* @param userName
	*            - login do bazy
	* @param password
	*            - has³o do bazy
	* @return - po³¹czenie z baz¹
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
			System.err.println("Blad przy ³¹czeniu z baz¹");
			System.out.println(e.getMessage());
		}
		return connection;
	}
	
	/**
	 * Metoda s³u¿y do po³¹czenia z MySQL bez wybierania konkretnej bazy
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
	 * Wykonanie kwerendy i przes³anie wyników do obiektu ResultSet
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
	 * Tworzenie obiektu Statement przesy³aj¹cego zapytania do bazy connection
	 * 
	 * @param connection - po³¹czenie z baz¹
	 * @return obiekt Statement przesy³aj¹cy zapytania do bazy
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
	 * Zamykanie po³¹czenia z baz¹ danych
	 * 
	 * @param connection - po³¹czenie z baz¹
	 * @param s - obiekt przesy³aj¹cy zapytanie do bazy
	 */
	public static void closeConnection(Connection connection, Statement s) {
		try {
			s.close();
			connection.close();
		} catch (SQLException e) {
			System.out.println("Bl¹d przy zamykaniu pol¹czenia " + e.toString());
			System.exit(4);
		}
	}
	
	
	/**Metoda tworz¹ca bazê danych w przypadku, gdy nie zosta³a ona jeszcze utworzona.
	 * @param nazwa
	 * 				- nazwa bazy danych
	 * @param listaKomend
	 * 				- lista ³añcuchów znakowych z poleceniami SQL modyfikuj¹cymi
	 * 				  bazê danych
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
			System.out.println("B³¹d tworzenia bazy danych");
		}
	}
	/**Metoda modyfikuj¹ca bazê danych
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
	
	
	/** Metoda pobieraj¹ca pytanie o okreœlonym numerze z bazy.
	 * 
	 * @param numer - numer pytania, które chcemy pobraæ
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
			System.out.println("B³¹d pobrania pytania z bazy.");
			Pytanie brak = new Pytanie("Brak tresci pytania.","pytanie");
			return brak;
		}
	}
	
	/** Metoda zapisuj¹ca odpowiedzi u¿ytkownika w bazie danych
	 * @param listaKomend - lista poleceñ SQL zapisuj¹cych odpowiedzi w bazie danych
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
			System.out.println("B³¹d dodawania odpowiedzi");
		}
	}
	
	/** Metoda pobieraj¹ca z bazy numer ostatniego pytania w celu oznaczenia go jako ostatnie
	 * 
	 * @return numery ostatniego pytania/ -1 gdy nie mo¿na po³¹czyæ siê z baz¹
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
			System.err.println("B³¹d pobierania numeru ostatniego pytania");
			e.printStackTrace();
			numer=-1;
		}
		catch (NullPointerException e){
			System.err.println("B³¹d pobierania numeru ostatniego pytania - Jeœli baza nie istnieje, zostanie teraz utworzona.");
			e.printStackTrace();
			numer=-1;
		}
		
		return numer;
	}
	
	/** Metoda pobieraj¹ca z bazy ID ostatniego zapisanego klienta w 
	 * celu rozpoczêcia nadawania numerów nowym klientom od numeru jeszcze nie istniej¹cego
	 * 
	 * @return numer ostatniego klienta lub 0, gdy baza nie istnieje/nie mo¿na siê z ni¹ po³¹czyæ
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
			System.err.println("B³¹d pobierania numeru ostatniego uzytkownika. Prawdopodonie nie ma ¿adnej zapisanej odpowiedzi.");
		}
		return numer;
	}
	
	/**Uniwersalna metoda wysy³aj¹ca do bazy zapytanie i zwracaj¹ca wynik
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
			System.err.println("B³¹d wykonania zapytania.");
			return null;
		}
	}
}