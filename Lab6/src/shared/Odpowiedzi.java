package shared;
/**@author Grzegorz M¹ka*/
import java.io.Serializable;
import java.util.ArrayList;

public class Odpowiedzi implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Integer> numeryOdpowiedzi;
	
	public Odpowiedzi(){
		numeryOdpowiedzi = new ArrayList<Integer>();
	}
	
	/** Metoda sprawdzaj¹ca, czy podana odpowiedŸ jest liczb¹ i czy pasuje do którejœ z dostêpnych odpowiedzi
	 * 
	 * @param odpowiedz - treœæ sprawdzanej odpowiedz
	 * @param pytanie - pytanie zawieraj¹ce listê wszystkich dostêpnych odpowiedzi
	 * @return true, jeœli odpowiedŸ zosta³a przyjêta i zapisana, lub false, jeœli odpowiedŸ nie mo¿e zostaæ przyjêta
	 * @throws Exception
	 */
	public boolean sprawdzOdpowiedz(String odpowiedz, Pytanie pytanie) throws Exception {
		int liczbaOdpowiedzi = pytanie.liczOdpowiedzi();
		int odpowiedzInt;
		try {
			odpowiedzInt = Integer.parseInt(odpowiedz);
			if (odpowiedzInt<=liczbaOdpowiedzi && odpowiedzInt>0){
				if (!duplikat(odpowiedzInt)){
					numeryOdpowiedzi.add(odpowiedzInt);
					return true;
				}
				return false;
			}
			else{
				if (odpowiedzInt!=0){
					System.err.println("Podana liczba nie odpowiada ¿adnej odpowiedzi.");
				}
			}
		}
		catch (Exception e){
			System.err.println("Podana przez klienta odpowiedŸ nie jest numerem");
		}
		return false;
	}
	
	/** Metoda sprawdzaj¹ca, czy dodawana odpowiedŸ nie znajduje siê ju¿ na liœcie
	 * 
	 * @param odpowiedzInt - liczba ca³kowita reprezentuj¹ca numer sprawdzanej odpowiedzi
	 * @return czy odpowiedŸ znajduje siê ju¿ na liœcie
	 */
	private boolean duplikat (int odpowiedzInt){
		if (numeryOdpowiedzi.size()>0){
			for (int i=0; i<numeryOdpowiedzi.size(); i++){
				if (odpowiedzInt == numeryOdpowiedzi.get(i)){
					System.err.println("Odpowiedz zostala juz wybrana wczesniej i nie zostanie teraz zapisana. \n Podaj nastepna odpowiedz: ");
					return true;
				}
			}
		}
		return false;
	}
	
	/**Metoda sprawdzaj¹ca, czy podana odpowiedŸ jest zerem
	 * 
	 * @param odpowiedz
	 * 				- ³añcuch znaków przekazany przez u¿ytkownika
	 * @return true/false
	 */
	public boolean zero(String odpowiedz){
		int odpowiedzInt;
		try {
			odpowiedzInt = Integer.parseInt(odpowiedz);
			if (odpowiedzInt==0){
				return true;
			}
		}
		catch (NumberFormatException e){
			//Wyj¹tek obs³u¿ony przy innej okazji
		}
		return false;
	}
	
	/** Zwraca ³añcuch znaków zawieraj¹cy polecenie SQL dodaj¹ce wpis o odpowiedzi klienta do bazy
	 * @param ID_klienta - Numer klienta, który udzieli³ odpowiedzi
	 * @param ID_pytania - Numer pytania, na które udzielona zosta³a odpowiedz
	 * @param ID_odpowiedzi - Numer odpowiedzi, która zosta³a wybrana
	 * @return polecenie SQL
	 */
	public String piszSQL_Odp(int ID_klienta, int ID_pytania, int ID_odpowiedzi){
		String tresc = "INSERT INTO odpowiedzi VALUES (" + Integer.toString(ID_klienta)+", " + Integer.toString(ID_pytania) + ", " + Integer.toString(ID_odpowiedzi) + ");";
		return tresc;
	}
	
	/** Zwraca ³añcuch znaków zawieraj¹cy polecenie SQL inkrementuj¹ce w bazie licznik przypisany do danej odpowiedzi
	 * @param ID_pytania - Numer pytania, na które udzielona zosta³a odpowiedz
	 * @param ID_odpowiedzi - Numer odpowiedzi, która zosta³a wybrana
	 * @return polecenie SQL
	 */
	public String piszSQL_Wyn(int ID_pytania, int ID_odpowiedzi){
		String tresc = "UPDATE wyniki SET licznik=licznik+1 WHERE ID_pytania="+Integer.toString(ID_pytania)+" AND ID_elementu="+Integer.toString(ID_odpowiedzi)+";";
		return tresc;
	}
	
	/** Tworzy polecenia SQL i ³¹czy je w listê, która zostanie póŸniej wykonana.
	 * @param ID_klienta - Numer klienta, który udzieli³ odpowiedzi
	 * @param ID_pytania - Numer pytania, na które udzielona zosta³a odpowiedz
	 * @return lista poleceñ SQL
	 */
	public ArrayList<String> piszListeSQL(int ID_klienta, int ID_pytania){
		ArrayList<String> lista= new ArrayList<String>();
		for (int i=0; i<numeryOdpowiedzi.size(); i++){
			lista.add(piszSQL_Odp(ID_klienta, ID_pytania, numeryOdpowiedzi.get(i)));
			lista.add(piszSQL_Wyn(ID_pytania, numeryOdpowiedzi.get(i)));
		}
		return lista;
	}
}
