package obd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

	private static String driver = "oracle.jdbc.driver.OracleDriver";
	private static String url = "jdbc:oracle:thin:@ora3.elka.pw.edu.pl:1521:ora3inf";
	private static String createTeacherTableQuery;
	private static String createStudentTableQuery;
	private static String createGradeTableQuery;
	private static String createSubjectTableQuery;
	private static String createGradingTableQuery;
	private static String showAllInformationAboutGradingQuery;

	private String user;
	private String password;
	private Scanner scanner;

	private Connection connection;
	private Statement statement;

	public void run() throws InterruptedException {
		scanner = new Scanner(System.in);
		System.out.println("Witaj w bazie danych!\n");

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			exitErrMessage("Nie mozna podlaczyc sterownika!");
		}

		try {
			connect();
			service();
		} catch (SQLException e) {
			if (e instanceof SQLException) {
				int code = e.getErrorCode();
				System.out.println();
				if (code == 20) // problem z internetem
					exitErrMessage("Problem z polaczeniem internetowym.");
				else if (code == 1017) {
					printErr("Bledny login lub haslo. Sprobuj ponownie.\n");
				} else
					exitErrMessage("Problem z polaczeniem do bazy danych. Kod bledu: " + code);
			}
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					exitErrMessage("Problem z zamknieciem polaczenia!");
				}
		}
	}

	private void connect() throws SQLException {
		boolean connected = false;
		while (!connected) {
			System.out.print("Podaj login: ");
			user = scanner.nextLine();
			System.out.print("Podaj haslo: ");
			password = scanner.nextLine();
			
			try {
				connection = DriverManager.getConnection(url, user, password);
				statement = connection.createStatement();
				connected = true;
			} catch (SQLException e) {
				int code = e.getErrorCode();
				if (code == 1017) {
					exitErrMessage("Bledny login lub haslo. Logowanie odrzucone.");
				} else if (code == 20) {
					exitErrMessage("Adapter sieci nie mogl ustanowic polaczenia.");
				} else {
					exitErrMessage("Nie mozna ustanowic polaczenia. Kod bledu: " + code);
				}
			}

		}
		System.out.println("\nPolaczono!");
	}

	private void service() throws InterruptedException {
		while (true) {
			System.out.println();
			System.out.println("MENU");
			System.out.println("1 | Utworz tabele");
			System.out.println("2 | Usun tabele");
			System.out.println("3 | Dodaj rekord do tebeli");
			System.out.println("4 | Wyswietl informacje o tabelach");
			System.out.println("5 | Wyswietl zawartosc tabeli");
			System.out.println("0 | Koniec");
			System.out.println("Twoj wybor:   ");
			String str = scanner.nextLine();
			while (str.length() == 0) {
				str = scanner.nextLine();
			}

			if (str.equals("1")) {
				option1();
			} else if (str.equals("2")) {
				option2();
			} else if (str.equals("3")) {
				option3();
			} else if (str.equals("4")) {
				showTablesInformation();
			} else if (str.equals("5")) {
				showTablesMenu();
			} else if (str.equals("0")) {
				System.out.println("Do widzenia!");
				System.exit(0);
			} else {
				System.out.println("Zly wybor, wybierz ponownie.\n");
			}
		}
	}

	// tworzenie tabel
	private void option1() throws InterruptedException {
		while (true) {
			System.out.println();
			System.out.println();
			System.out.println("TWORZENIE TABEL");
			System.out.println("1 | Utworz wszystkie tabele");
			System.out.println("2 | Utworz tabele NAUCZYCIEL");
			System.out.println("3 | Utworz tabele UCZEN");
			System.out.println("4 | Utworz tabele PRZEDMIOT");
			System.out.println("5 | Utworz tabele OCENA");
			System.out.println("6 | Utworz tabele OCENIANIE");
			System.out.println("0 | Powrot");
			System.out.println("Twoj wybor:   ");
			String str = scanner.nextLine();
			while (str.length() == 0) {
				str = scanner.nextLine();
			}

			if (str.equals("1")) {
				createTable("nauczyciel", createTeacherTableQuery);
				createTable("uczen", createStudentTableQuery);
				createTable("przedmiot", createSubjectTableQuery);
				createTable("ocena", createGradeTableQuery);
				createTable("ocenianie", createGradingTableQuery);
			} else if (str.equals("2")) {
				createTable("nauczyciel", createTeacherTableQuery);
			} else if (str.equals("3")) {
				createTable("uczen", createStudentTableQuery);
			} else if (str.equals("4")) {
				createTable("przedmiot", createSubjectTableQuery);
			} else if (str.equals("5")) {
				createTable("ocena", createGradeTableQuery);
			} else if (str.equals("6")) {
				createTable("ocenianie", createGradingTableQuery);
			} else if (str.equals("0")) {
				return;
			} else {
				System.out.println("Zly wybor, wybierz ponownie.\n");
			}
		}
	}

	private void option2() {
		while (true) {
			System.out.println();
			System.out.println("USUWANIE TABEL");
			System.out.println("1 | Usun tabele NAUCZYCIEL");
			System.out.println("2 | Usun tabele UCZEN");
			System.out.println("3 | Usun tabele PRZEDMIOT");
			System.out.println("4 | Usun tabele OCENA");
			System.out.println("5 | Usun tabele OCENIANIE");
			System.out.println("0 | Powrot");
			System.out.println("Twoj wybor:");
			String str = scanner.nextLine();
			while (str.length() == 0) {
				str = scanner.nextLine();
			}

			if (str.equals("1")) {
				tryDropTable("nauczyciel");
			} else if (str.equals("2")) {
				tryDropTable("uczen");
			} else if (str.equals("3")) {
				tryDropTable("przedmiot");
			} else if (str.equals("4")) {
				tryDropTable("ocena");
			} else if (str.equals("5")) {
				dropTable("ocenianie");
			} else if (str.equals("0")) {
				return;
			} else {
				System.out.println("Zly wybor, wybierz ponownie.\n");
			}
		}
	}

	// doawanie rekordow
	private void option3() {
		while (true) {
			System.out.println();
			System.out.println("DODAJ REKORD DO TABELI");
			System.out.println("1 | Dodaj nowego nauczyciela");
			System.out.println("2 | Dodaj nowego ucznia");
			System.out.println("3 | Dodaj nowy przedmiot");
			System.out.println("4 | Dodaj nowa ocene");
			System.out.println("5 | Wystaw ocene");
			System.out.println("6 | Uzupelnij tabele przykladowymi danymi");
			System.out.println("0 | Powrot");
			System.out.println("Twoj wybor:");
			String str = scanner.nextLine();
			while (str.length() == 0) {
				str = scanner.nextLine();
			}

			if (str.equals("1")) {
				addNewTeacher();
			} else if (str.equals("2")) {
				addNewStudent();
			} else if (str.equals("3")) {
				addNewSubject();
			} else if (str.equals("4")) {
				addNewGrade();
			} else if (str.equals("5")) {
				addNewGrading();
			} else if (str.equals("6")) {
				supplyTablesWithExemplaryValues();
			} else if (str.equals("0")) {
				return;
			} else {
				System.out.println("Zly wybor, wybierz ponownie.\n");
			}
		}
	}

	// dodawanie nowego nauczyciela do bazy
	private void addNewTeacher() {
		System.out.println();
		System.out.println("DODAWANIE NOWEGO NAUCZYCIELA");
		String nrIDStr;
		float nrID;
		String lastName;
		String name;
		while (true) {
			System.out.println("Podaj numer ID:");
			nrIDStr = scanner.nextLine();
			if (nrIDStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (nrIDStr.matches("\\d+(\\.\\d+)?")) {
				try {
					nrID = Float.parseFloat(nrIDStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}
		while (true) {
			System.out.println("Podaj nazwisko:");
			lastName = scanner.nextLine();
			if (lastName.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (lastName.length() > 30) {
				System.out.println("Wartosc zbyt duza dla kolumny NAZWISKO_NAUCZYCIELA (obecna: " + lastName.length()
						+ ", maksymalna: 30).");
			} else {
				break;
			}
		}
		while (true) {
			System.out.println("Podaj imie:");
			name = scanner.nextLine();
			if (name.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (name.length() > 20) {
				System.out.println("Wartosc zbyt duza dla kolumny IMIE_NAUCZYCIELA (obecna: " + name.length()
						+ ", maksymalna: 20).");
			} else {
				break;
			}
		}

		insertNewPerson(nrID, lastName, name, "Nauczyciel");
	}

	// dodawanie nowego studenta do bazy
	private void addNewStudent() {
		System.out.println();
		System.out.println("DODAWANIE NOWEGO STUDENTA");
		String nrIDStr;
		float nrID;
		String lastName;
		String name;
		while (true) {
			System.out.println("Podaj numer ID:");
			nrIDStr = scanner.nextLine();
			if (nrIDStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (nrIDStr.matches("\\d+(\\.\\d+)?")) {
				try {
					nrID = Float.parseFloat(nrIDStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}
		while (true) {
			System.out.println("Podaj nazwisko:");
			lastName = scanner.nextLine();
			if (lastName.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (lastName.length() > 30) {
				System.out.println("Wartosc zbyt duza dla kolumny NAZWISKO_UCZNIA (obecna: " + lastName.length()
						+ ", maksymalna: 30).");
			} else {
				break;
			}
		}
		while (true) {
			System.out.println("Podaj imie:");
			name = scanner.nextLine();
			if (name.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (name.length() > 20) {
				System.out.println(
						"Wartosc zbyt duza dla kolumny IMIE_UCZNIA (obecna: " + name.length() + ", maksymalna: 20).");
			} else {
				break;
			}
		}

		insertNewPerson(nrID, lastName, name, "Uczen");
	}

	// dodawanie nowego przedmiotu do bazy
	private void addNewSubject() {
		System.out.println();
		System.out.println("DODAWANIE NOWEGO PRZEDMIOTU");
		String nrIDStr;
		float nrID;
		String name;
		while (true) {
			System.out.println("Podaj numer ID:");
			nrIDStr = scanner.nextLine();
			if (nrIDStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (nrIDStr.matches("\\d+(\\.\\d+)?")) {
				try {
					nrID = Float.parseFloat(nrIDStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}
		while (true) {
			System.out.println("Podaj nazwe:");
			name = scanner.nextLine();
			if (name.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (name.length() > 20) {
				System.out.println("Wartosc zbyt duza dla kolumny NAZWA_PRZEDMIOTU (obecna: " + name.length()
						+ ", maksymalna: 20).");
			} else {
				break;
			}
		}

		insertNewSubject(nrID, name);
	}

	// dodawanie nowej oceny do bazy
	private void addNewGrade() {
		System.out.println();
		System.out.println("DODAWANIE NOWEJ OCENY");
		String nrIDStr;
		float nrID;
		String descriptiveValue;
		String valStr;
		float val;
		while (true) {
			System.out.println("Podaj numer ID:");
			nrIDStr = scanner.nextLine();
			if (nrIDStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (nrIDStr.matches("\\d+(\\.\\d+)?")) {
				try {
					nrID = Float.parseFloat(nrIDStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}
		while (true) {
			System.out.println("Podaj wartosc opisowa:");
			descriptiveValue = scanner.nextLine();
			if (descriptiveValue.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (descriptiveValue.length() > 20) {
				System.out.println("Wartosc zbyt duza dla kolumny WARTOSC_OPISOWA (obecna: " + descriptiveValue.length()
						+ ", maksymalna: 20).");
			} else {
				break;
			}
		}
		while (true) {
			System.out.println("Podaj wartosc numeryczna:");
			valStr = scanner.nextLine();
			if (valStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (valStr.matches("\\d+(\\.\\d+)?")) {
				try {
					val = Float.parseFloat(valStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}

		insertNewGrade(nrID, descriptiveValue, val);
	}

	private void addNewGrading() {
		System.out.println();
		System.out.println("NOWE OCENIANIE");
		String idTeacherStr;
		int idTeacher;
		String idStudentStr;
		int idStudent;
		String idSubjectStr;
		int idSubject;
		String idGradeStr;
		int idGrade;
		String type;

		while (true) {
			System.out.println("Podaj numer ID nauczyciela:");
			idTeacherStr = scanner.nextLine();
			if (idTeacherStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (idTeacherStr.matches("\\d+")) {
				try {
					idTeacher = Integer.parseInt(idTeacherStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}

		while (true) {
			System.out.println("Podaj numer ID ucznia:");
			idStudentStr = scanner.nextLine();
			if (idStudentStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (idStudentStr.matches("\\d+")) {
				try {
					idStudent = Integer.parseInt(idStudentStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}

		while (true) {
			System.out.println("Podaj numer ID przedmiotu:");
			idSubjectStr = scanner.nextLine();
			if (idSubjectStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (idSubjectStr.matches("\\d+")) {
				try {
					idSubject = Integer.parseInt(idSubjectStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}

		while (true) {
			System.out.println("Podaj numer ID oceny:");
			idGradeStr = scanner.nextLine();
			if (idGradeStr.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (idGradeStr.matches("\\d+")) {
				try {
					idGrade = Integer.parseInt(idGradeStr);
					break;
				} catch (NumberFormatException e) {
					System.out.println("Niepoprawna liczba.");
				}
			} else {
				System.out.println("Niepoprawna liczba.");
			}
		}

		while (true) {
			System.out.println("Podaj rodzaj oceny \"C\"-czastkowa, \"S\"-semestralna:");
			type = scanner.nextLine();
			if (type.length() <= 0) {
				System.out.println("Nie mozna wstawic wartosci NULL.");
			} else if (type.length() > 1) {
				System.out.println(
						"Wartosc zbyt duza dla kolumny RODZAJ_OCENY (obecna: " + type.length() + ", maksymalna: 1).");
			} else if (type.equalsIgnoreCase("C") || type.equalsIgnoreCase("S")) {
				type = type.toUpperCase();
				break;
			} else {
				System.out.println("Rodzaj oceny to \"C\" lub \"S\".");
			}
		}

		String createNewGradingQuery = "INSERT INTO ocenianie VALUES (" + idTeacher + ", " + idStudent + ", "
				+ idSubject + ", " + idGrade + ", " + "'" + type + "')";
		try {
			ResultSet setTeacher = statement.executeQuery("SELECT * FROM nauczyciel WHERE idn=" + idTeacher);
			if (!setTeacher.next()) {
				System.out.println("Naruszono wiezy unikatowe. Nie znaleziono klucza nadrzędnego OCENIANIE_FK1");
				return;
			}

			ResultSet setStudent = statement.executeQuery("SELECT * FROM uczen WHERE idu=" + idStudent);
			if (!setStudent.next()) {
				System.out.println("Naruszono wiezy unikatowe. Nie znaleziono klucza nadrzędnego OCENIANIE_FK2");
				return;
			}

			ResultSet setSubject = statement.executeQuery("SELECT * FROM przedmiot WHERE idp=" + idSubject);
			if (!setSubject.next()) {
				System.out.println("Naruszono wiezy unikatowe. Nie znaleziono klucza nadrzędnego OCENIANIE_FK3");
				return;
			}

			ResultSet setGrade = statement.executeQuery("SELECT * FROM ocena WHERE ido=" + idGrade);
			if (!setGrade.next()) {
				System.out.println("Naruszono wiezy unikatowe. Nie znaleziono klucza nadrzędnego OCENIANIE_FK4");
				return;
			}

			statement.execute(createNewGradingQuery);
			System.out.println("Dodano");

		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 904) {
				printErr("Niepoprawny identyfikator.");
			} else if (code == 942) {
				printErr("Tabela lub perspektywa nie istnieje.");
			} else {
				printErr("Problem podczas dodawania elementu do bazy danych. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
	}

	private boolean insertNewPerson(float nrID, String lastName, String name, String table) {
		String createNewTeacherQuery = "INSERT INTO " + table.toLowerCase() +" VALUES (" + nrID + ", '" + lastName + "', '" + name
				+ "')";
		try {
			ResultSet set = statement.executeQuery("SELECT * FROM " + table.toLowerCase() + " WHERE id" + table.toLowerCase().charAt(0) + "=" + nrID);
			if (set.next()) {
				System.out.println("Naruszono wiezy unikatowe. " + table + " o takim numerze ID juz istnieje w bazie.");
			} else {
				statement.execute(createNewTeacherQuery);
				System.out.println("Dodano");
				return true;
			}
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 904) {
				printErr("Niepoprawny identyfikator.");
			} else if (code == 942) {
				printErr("Tabela lub perspektywa nie istnieje.");
			} else {
				printErr("Problem podczas dodawania elementu do bazy danych. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
		return false;
	}

	private boolean insertNewSubject(float nrID, String name) {
		String createNewSubjectQuery = "INSERT INTO przedmiot VALUES (" + nrID + ", '" + name + "')";
		try {
			ResultSet set = statement.executeQuery("SELECT * FROM przedmiot WHERE idp=" + nrID);
			if (set.next()) {
				System.out.println("Naruszono wiezy unikatowe. Przedmiot o takim numerze ID juz istnieje w bazie.");
			} else {
				statement.execute(createNewSubjectQuery);
				System.out.println("Dodano");
				return true;
			}
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 904) {
				printErr("Niepoprawny identyfikator.");
			} else if (code == 942) {
				printErr("Tabela lub perspektywa nie istnieje.");
			} else {
				printErr("Problem podczas dodawania elementu do bazy danych. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
		return false;
	}

	private boolean insertNewGrade(float nrID, String descriptiveValue, float val) {
		String createNewGradeQuery = "INSERT INTO ocena VALUES (" + nrID + ", '" + descriptiveValue + "', " + val + ")";
		try {
			ResultSet set = statement.executeQuery("SELECT * FROM ocena WHERE ido=" + nrID);
			if (set.next()) {
				System.out.println("Naruszono wiezy unikatowe. Ocena o takim numerze ID juz istnieje w bazie.");
			} else {
				statement.execute(createNewGradeQuery);
				System.out.println("Dodano");
				return true;
			}
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 904) {
				printErr("Niepoprawny identyfikator.");
			} else if (code == 942) {
				printErr("Tabela lub perspektywa nie istnieje.");
			} else {
				printErr("Problem podczas dodawania elementu do bazy danych. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
		return false;
	}

	private void supplyTablesWithExemplaryValues() {
		System.out.println("\nDodawanie nowych nauczycieli.");
		insertNewPerson(1, "Kowalska", "Jolanta", "Nauczyciel");
		insertNewPerson(2, "Pawlowski", "Przemyslaw", "Nauczyciel");
		insertNewPerson(3, "Marcinkowski", "Adam", "Nauczyciel");
		insertNewPerson(4, "Nowak", "Zofia", "Nauczyciel");
		insertNewPerson(5, "Adamczyk", "Paulina", "Nauczyciel");

		System.out.println("\nDodawanie nowych uczniow.");
		insertNewPerson(1, "Kowalik", "Piotr", "Uczen");
		insertNewPerson(2, "Matysiak", "Mateusz", "Uczen");
		insertNewPerson(3, "Dabrowski", "Jas", "Uczen");
		insertNewPerson(4, "Baranowska", "Katarzyna", "Uczen");
		insertNewPerson(5, "Gruszka", "Marcelina", "Uczen");

		System.out.println("\nDodawanie nowych przedmiotow.");
		insertNewSubject(1, "Matematyka");
		insertNewSubject(2, "Przyroda");
		insertNewSubject(3, "Fizyka");
		insertNewSubject(4, "Informatyka");
		insertNewSubject(5, "Historia");

		System.out.println("\nDodawanie nowych ocen.");
		insertNewGrade(1, "Niedostateczny", 1);
		insertNewGrade(2, "Niedostateczny Plus", 1.5f);
		insertNewGrade(3, "Niedopuszczalny", 2);
		insertNewGrade(4, "Niedopuszczalny Plus", 2.5f);
		insertNewGrade(5, "Dostateczny", 3);
		insertNewGrade(6, "Dostateczny Plus", 3.5f);
		insertNewGrade(7, "Dobry", 4);
		insertNewGrade(8, "Dobry Plus", 4.5f);
		insertNewGrade(9, "Bardzo dobry", 5);
		insertNewGrade(10, "Bardzo dobry plus", 5.5f);
		insertNewGrade(11, "Celujacy", 6);
	}

	private void createTable(String name, String query) throws InterruptedException {
		try {
			statement.execute(query);
			System.out.println("Utworzono tabele o nazwie " + name + ".\n");
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (e.getErrorCode() == 955) {
				System.out.println("Tabela o nazwie " + name.toUpperCase() + " już istnieje.\n"
						+ "Czy chcesz ja utworzyc na nowo? [T/N]");
				String str = scanner.next();
				if (str.equalsIgnoreCase("t")) {
					try {
						boolean droped = tryDropTable(name);
						if (droped) {
							statement.execute(query);
						} else {
							return;
						}
					} catch (SQLException e1) {
						printErr(" tworzenia tabeli " + str.toUpperCase() + " na nowo.");
					}
				}
			} else {
				printErr("Problem z polaczeniem podczas tworzenia tabeli " + name + "." + " Kod bledu: " + code);
			}
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e1) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
	}

	private boolean tryDropTable(String name) {
		try {
			statement.executeQuery("SELECT COUNT(*) FROM ocenianie");
			System.out.println("Tabela ma unikatowe/glowne klucze, do ktorych odwoluja sie obce klucze.");
			return false;
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 904) {
				printErr("Niepoprawny identyfikator.");
			} else if (code == 942) {
				return dropTable(name);
			} else {
				printErr("Problem podczas usuwania tabeli z bazy danych. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
		return false;
	}

	private boolean dropTable(String name) {
		try {
			statement.executeQuery("DROP TABLE " + name);
			System.out.println("Usunieto");
			return true;
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 904) {
				printErr("Niepoprawny identyfikator.");
			} else if (code == 942) {
				printErr("Tabela lub perspektywa nie istnieje.");
			} else {
				printErr("Problem podczas usuwania tabeli z bazy danych. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
		return false;
	}

	private void showTablesInformation() {
		System.out.println();
		try {
			ResultSet setTeacher = statement.executeQuery("SELECT COUNT(*) FROM nauczyciel");
			setTeacher.next();
			System.out.println("W tabeli NAUCZYCIEL znajduje sie " + setTeacher.getInt(1) + " pozycji.");
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 942) {
				System.out.println("Tabela NAUCZYCIEL nie istnieje.");
			} else {
				printErr("Problem podczas pobierania danych z bazy. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}

		try {
			ResultSet setStudent = statement.executeQuery("SELECT COUNT(*) FROM uczen");
			setStudent.next();
			System.out.println("W tabeli UCZEN znajduje sie " + setStudent.getInt(1) + " pozycji.");
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 942) {
				System.out.println("Tabela UCZEN nie istnieje.");
			} else {
				printErr("Problem podczas pobierania danych z bazy. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}

		try {
			ResultSet setSubject = statement.executeQuery("SELECT COUNT(*) FROM przedmiot");
			setSubject.next();
			System.out.println("W tabeli PRZEDMIOT znajduje sie " + setSubject.getInt(1) + " pozycji.");
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 942) {
				System.out.println("Tabela PRZEDMIOT nie istnieje.");
			} else {
				printErr("Problem podczas pobierania danych z bazy. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}

		try {
			ResultSet setGrade = statement.executeQuery("SELECT COUNT(*) FROM ocena");
			setGrade.next();
			System.out.println("W tabeli OCENA znajduje sie " + setGrade.getInt(1) + " pozycji.");
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 942) {
				System.out.println("Tabela OCENA nie istnieje.");
			} else {
				printErr("Problem podczas pobierania danych z bazy. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}

		try {
			ResultSet setGrading = statement.executeQuery("SELECT COUNT(*) FROM ocenianie");
			setGrading.next();
			System.out.println("W tabeli OCENIANIE znajduje sie " + setGrading.getInt(1) + " pozycji.");
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 942) {
				System.out.println("Tabela OCENIANIE nie istnieje.");
			} else {
				printErr("Problem podczas pobierania danych z bazy. Kod bledu: " + code);
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}

	}

	private void showTablesMenu() {
		while (true) {
			System.out.println();
			System.out.println("MENU");
			System.out.println("1 | Pokaz tabele NAUCZYCIEL");
			System.out.println("2 | Pokaz tabele UCZEN");
			System.out.println("3 | Pokaz tabele PRZEDMIOT");
			System.out.println("4 | Pokaz tabele OCENA");
			System.out.println("5 | Pokaz tabele OCENIANIE");
			System.out.println("6 | Pokaz tabele OCENIANIE z wypelnionymi danymi");
			System.out.println("0 | Powrot");
			System.out.println("Twoj wybor:   ");
			String str = scanner.nextLine();
			while (str.length() == 0) {
				str = scanner.nextLine();
			}

			if (str.equals("1")) {
				showTable("nauczyciel", "idn", null);
			} else if (str.equals("2")) {
				showTable("uczen", "idu", null);
			} else if (str.equals("3")) {
				showTable("przedmiot", "idp", null);
			} else if (str.equals("4")) {
				showTable("ocena", "ido", null);
			} else if (str.equals("5")) {
				showTable("ocenianie", null, null);
			} else if (str.equals("6")) {
				showTable("ocenianie", null, showAllInformationAboutGradingQuery);
			} else if (str.equals("0")) {
				return;
			} else {
				System.out.println("Zly wybor, wybierz ponownie.\n");
			}
			System.out.println();
		}
	}

	private void showTable(String tableName, String order, String query) {
		System.out.println();
		try {
			ResultSet set;
			if (query == null) {
				if (order == null) {
					set = statement.executeQuery("SELECT * FROM " + tableName.toLowerCase());
				} else {
					set = statement.executeQuery("SELECT * FROM " + tableName.toLowerCase() + " ORDER BY "
							+ tableName.toLowerCase() + "." + order);
				}
			} else {
				set = statement.executeQuery(query);
			}
			ResultSetMetaData meta = set.getMetaData();
			int col = meta.getColumnCount();
			List<String[]> dataList = new ArrayList<String[]>();
			String[] headers = new String[col];
			int[] headersMax = new int[col];

			for (int i = 1; i <= col; i++) {
				headers[i - 1] = meta.getColumnName(i);
				headersMax[i - 1] = headers[i - 1].length();
			}
			dataList.add(headers);
			while (set.next()) {
				String[] datas = new String[col];
				for (int i = 1; i <= col; i++) {
					Object o = set.getObject(i);
					if (o != null) {
						datas[i - 1] = o.toString();
						headersMax[i - 1] = datas[i - 1].length() > headersMax[i - 1] ? datas[i - 1].length()
								: headersMax[i - 1];
					} else {
						datas[i - 1] = "null";
						headersMax[i - 1] = datas[i - 1].length() > headersMax[i - 1] ? datas[i - 1].length()
								: headersMax[i - 1];
					}
				}
				dataList.add(datas);
			}

			for (String[] strs : dataList) {
				for (int i = 0; i < col; i++) {
					String format = "|%-" + headersMax[i] + "s|";
					System.out.print(String.format(format, strs[i]));
				}
				System.out.println();
			}
		} catch (SQLException e) {
			int code = e.getErrorCode();
			if (code == 17002) {
				printErr("Przekroczono czas oczekiwania na polaczenie.");
			} else if (code == 942) {
				System.out.println("Tabela " + tableName.toUpperCase() + " nie istnieje.");
			} else {
				printErr("Problem podczas pobierania danych z bazy. Kod bledu: " + code);
				e.printStackTrace();
			}
		} finally {
			try {
				if (connection.isClosed())
					connect();
			} catch (SQLException e) {
				exitErrMessage("Blad podczas ponownej proby polaczenia. Kod bledu: " + e.getErrorCode());
			}
		}
	}

	private void exitErrMessage(String mes) {
		System.err.println(mes);
		System.err.println("Koniec programu");
		System.exit(0);
	}

	private void printErr(String mes) {
		System.err.println(mes);
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new Main().run();
	}

	static {
		createTeacherTableQuery = "CREATE TABLE nauczyciel " + "(idn integer not null, "
				+ "nazwisko_nauczyciela char(30) not null, " + "imie_nauczyciela char(20) not null)";
		createStudentTableQuery = "CREATE TABLE uczen " + "(idu integer not null, "
				+ "nazwisko_ucznia char(30) not null, " + "imie_ucznia char(20) not null)";
		createGradeTableQuery = "CREATE TABLE ocena " + "(ido integer not null, "
				+ "wartosc_opisowa char(20) not null, " + "wartosc_numeryczna float not null)";
		createSubjectTableQuery = "CREATE TABLE przedmiot " + "(idp integer not null, "
				+ "nazwa_przedmiotu char(20) not null)";
		createGradingTableQuery = "CREATE TABLE ocenianie " + "(idn integer not null, " + "idu integer not null, "
				+ "idp integer not null, " + "ido integer not null, " + "rodzaj_oceny char(1) not null)";
		showAllInformationAboutGradingQuery = "SELECT nazwisko_nauczyciela, imie_nauczyciela, nazwisko_ucznia, imie_ucznia, "
				+ "nazwa_przedmiotu, wartosc_opisowa, wartosc_numeryczna, rodzaj_oceny FROM nauczyciel n, "
				+ "uczen u, przedmiot p, ocena o, ocenianie oe WHERE oe.idn=n.idn AND oe.idu=u.idu AND "
				+ "oe.idp=p.idp AND oe.ido=o.ido";
	}
}
