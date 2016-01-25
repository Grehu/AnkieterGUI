package shared;
/**@author Grzegorz M¹ka*/
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**Klasa Pytanie przechowuje treœæ pytania i odpowiedzi w formie hashmapy, w której ka¿dej odpowiedzi przyporz¹dkowany jest odpowiedni numer*/
public class Pytanie implements Serializable{

	private static final long serialVersionUID = 1L;

	public String tresc;

	public Map<Integer, String> odpowiedzi;
	public int licznikOdpowiedzi;
	public boolean wielokrotne;
	public boolean ostatnie;
	
	public Pytanie (String tresc, String typ){
		this.tresc=tresc;
		odpowiedzi = new HashMap<Integer, String>();
		licznikOdpowiedzi=0;
		if (typ.equals("pytanie")){
			wielokrotne=false;
		}
		if (typ.equals("pytanie wielokrotnego wyboru")){
			wielokrotne=true;
		}
		ostatnie = false;
	}
	
	/**Metoda dodaj¹ca odpowiedzi do hashmapy
	 * @param odpowiedz - Ci¹g znaków bêd¹cy treœci¹ odpowiedzi
	 */
	public void dodajOdpowiedz(String odpowiedz){
		odpowiedzi.put(licznikOdpowiedzi+1, odpowiedz);
		licznikOdpowiedzi++;
	}
	/** Metoda kasuj¹ca odpowiedŸ z listy odpowiedzi.
	 * @param numer - Numer odpowiedzi, któr¹ chcemy skasowaæ z listy wszystkich dostêpnych
	 */
	public void skasujOdpowiedz(int numer){
		odpowiedzi.remove(numer);
	}
	
	/** Metoda zwracaj¹ca true, gdy pytanie jest pytaniem wielokrotnego wyboru
	 * @return true/false
	 */
	public boolean wielokrotnosc(){
		return wielokrotne;
	}
	
	/** Metoda zwracaj¹ca treœæ pytania*/
	public String pobierzPytanie(){
		return tresc;
	}
	
	/**Metoda zwracaj¹ca odpowiedŸ o podanym numerze z hashmapy dostêpnych odpowiedzi
	 * @param numer - numer odpowiedzi, która ma zostaæ zwrócona
	 * @return treœæ odpowiedzi/pusty ³añcuch znaków
	 */
	public String pobierzOdpowiedz(int numer) {
		try{
			return odpowiedzi.get(numer);
		}
		catch (Exception e){
			return "";
		}
	}
	
	/** Metoda zwracaj¹ca liczbê wszystkich odpowiedzi na pytanie*/
	public int liczOdpowiedzi(){
		return odpowiedzi.size();
	}
	
	/**Metoda zwracaj¹ca blok tekstu z³o¿ony z treœci pytania i wszytskich odpowiedzi*/
	public String piszWiadomosc(){
		String wiadomosc=tresc+"\n";
		for (int i=1; i<=this.liczOdpowiedzi(); i++){
			wiadomosc = wiadomosc + Integer.toString(i) + ". " + this.pobierzOdpowiedz(i) + "\n";
		}
		return wiadomosc;
	}
	
	/**Metoda zwracaj¹ca jednowymiarow¹ tablicê ³añcuchów tekstowych, 
	 * w której sk³ad wchodz¹ treœæ pytania i treœci odpowiedzi
	 */
	public String[] piszPytanie(){
		int rozmiar = liczOdpowiedzi()+1;
		String[] linieTekstu = new String[rozmiar];
		linieTekstu[0] = tresc;
		for (int i=1; i<rozmiar; i++){
			linieTekstu[i] = Integer.toString(i) + ". " + pobierzOdpowiedz(i);
		}
		return linieTekstu;
	}

}
