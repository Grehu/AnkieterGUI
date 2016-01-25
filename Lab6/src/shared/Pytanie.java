package shared;
/**@author Grzegorz M�ka*/
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**Klasa Pytanie przechowuje tre�� pytania i odpowiedzi w formie hashmapy, w kt�rej ka�dej odpowiedzi przyporz�dkowany jest odpowiedni numer*/
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
	
	/**Metoda dodaj�ca odpowiedzi do hashmapy
	 * @param odpowiedz - Ci�g znak�w b�d�cy tre�ci� odpowiedzi
	 */
	public void dodajOdpowiedz(String odpowiedz){
		odpowiedzi.put(licznikOdpowiedzi+1, odpowiedz);
		licznikOdpowiedzi++;
	}
	/** Metoda kasuj�ca odpowied� z listy odpowiedzi.
	 * @param numer - Numer odpowiedzi, kt�r� chcemy skasowa� z listy wszystkich dost�pnych
	 */
	public void skasujOdpowiedz(int numer){
		odpowiedzi.remove(numer);
	}
	
	/** Metoda zwracaj�ca true, gdy pytanie jest pytaniem wielokrotnego wyboru
	 * @return true/false
	 */
	public boolean wielokrotnosc(){
		return wielokrotne;
	}
	
	/** Metoda zwracaj�ca tre�� pytania*/
	public String pobierzPytanie(){
		return tresc;
	}
	
	/**Metoda zwracaj�ca odpowied� o podanym numerze z hashmapy dost�pnych odpowiedzi
	 * @param numer - numer odpowiedzi, kt�ra ma zosta� zwr�cona
	 * @return tre�� odpowiedzi/pusty �a�cuch znak�w
	 */
	public String pobierzOdpowiedz(int numer) {
		try{
			return odpowiedzi.get(numer);
		}
		catch (Exception e){
			return "";
		}
	}
	
	/** Metoda zwracaj�ca liczb� wszystkich odpowiedzi na pytanie*/
	public int liczOdpowiedzi(){
		return odpowiedzi.size();
	}
	
	/**Metoda zwracaj�ca blok tekstu z�o�ony z tre�ci pytania i wszytskich odpowiedzi*/
	public String piszWiadomosc(){
		String wiadomosc=tresc+"\n";
		for (int i=1; i<=this.liczOdpowiedzi(); i++){
			wiadomosc = wiadomosc + Integer.toString(i) + ". " + this.pobierzOdpowiedz(i) + "\n";
		}
		return wiadomosc;
	}
	
	/**Metoda zwracaj�ca jednowymiarow� tablic� �a�cuch�w tekstowych, 
	 * w kt�rej sk�ad wchodz� tre�� pytania i tre�ci odpowiedzi
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
