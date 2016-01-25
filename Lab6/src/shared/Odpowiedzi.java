package shared;
/**@author Grzegorz M�ka*/
import java.io.Serializable;
import java.util.ArrayList;

public class Odpowiedzi implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Integer> numeryOdpowiedzi;
	
	public Odpowiedzi(){
		numeryOdpowiedzi = new ArrayList<Integer>();
	}
	
	/** Metoda sprawdzaj�ca, czy podana odpowied� jest liczb� i czy pasuje do kt�rej� z dost�pnych odpowiedzi
	 * 
	 * @param odpowiedz - tre�� sprawdzanej odpowiedz
	 * @param pytanie - pytanie zawieraj�ce list� wszystkich dost�pnych odpowiedzi
	 * @return true, je�li odpowied� zosta�a przyj�ta i zapisana, lub false, je�li odpowied� nie mo�e zosta� przyj�ta
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
					System.err.println("Podana liczba nie odpowiada �adnej odpowiedzi.");
				}
			}
		}
		catch (Exception e){
			System.err.println("Podana przez klienta odpowied� nie jest numerem");
		}
		return false;
	}
	
	/** Metoda sprawdzaj�ca, czy dodawana odpowied� nie znajduje si� ju� na li�cie
	 * 
	 * @param odpowiedzInt - liczba ca�kowita reprezentuj�ca numer sprawdzanej odpowiedzi
	 * @return czy odpowied� znajduje si� ju� na li�cie
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
	
	/**Metoda sprawdzaj�ca, czy podana odpowied� jest zerem
	 * 
	 * @param odpowiedz
	 * 				- �a�cuch znak�w przekazany przez u�ytkownika
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
			//Wyj�tek obs�u�ony przy innej okazji
		}
		return false;
	}
	
	/** Zwraca �a�cuch znak�w zawieraj�cy polecenie SQL dodaj�ce wpis o odpowiedzi klienta do bazy
	 * @param ID_klienta - Numer klienta, kt�ry udzieli� odpowiedzi
	 * @param ID_pytania - Numer pytania, na kt�re udzielona zosta�a odpowiedz
	 * @param ID_odpowiedzi - Numer odpowiedzi, kt�ra zosta�a wybrana
	 * @return polecenie SQL
	 */
	public String piszSQL_Odp(int ID_klienta, int ID_pytania, int ID_odpowiedzi){
		String tresc = "INSERT INTO odpowiedzi VALUES (" + Integer.toString(ID_klienta)+", " + Integer.toString(ID_pytania) + ", " + Integer.toString(ID_odpowiedzi) + ");";
		return tresc;
	}
	
	/** Zwraca �a�cuch znak�w zawieraj�cy polecenie SQL inkrementuj�ce w bazie licznik przypisany do danej odpowiedzi
	 * @param ID_pytania - Numer pytania, na kt�re udzielona zosta�a odpowiedz
	 * @param ID_odpowiedzi - Numer odpowiedzi, kt�ra zosta�a wybrana
	 * @return polecenie SQL
	 */
	public String piszSQL_Wyn(int ID_pytania, int ID_odpowiedzi){
		String tresc = "UPDATE wyniki SET licznik=licznik+1 WHERE ID_pytania="+Integer.toString(ID_pytania)+" AND ID_elementu="+Integer.toString(ID_odpowiedzi)+";";
		return tresc;
	}
	
	/** Tworzy polecenia SQL i ��czy je w list�, kt�ra zostanie p�niej wykonana.
	 * @param ID_klienta - Numer klienta, kt�ry udzieli� odpowiedzi
	 * @param ID_pytania - Numer pytania, na kt�re udzielona zosta�a odpowiedz
	 * @return lista polece� SQL
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
