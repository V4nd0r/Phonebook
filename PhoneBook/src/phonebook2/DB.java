
package phonebook2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DB {
    final String URL = "jdbc:derby:sampleDB; create=true"; 
    final String USERNAME = ""; // ezeket nem hasznéljuk, mivel belső lesz az adatbázis
    final String PASSWORD = ""; // külső DB-nél azonban nagyonis kellenének!!!
    
        
    Connection conn = null;
    Statement createStatement = null;
    DatabaseMetaData dbmd = null;
    
    public DB (){
          
        try { 
            conn = DriverManager.getConnection(URL);
            System.out.println("A híd létrejött!");
        } catch (SQLException ex) {
            System.out.println("Gebasz van a híddal!");
            System.out.println (""+ ex);
        }
        
        //létrehozunk egy statementet
        if (conn != null){
        try {
        createStatement = conn.createStatement();
        } catch (SQLException ex) {
            System.out.println("Gebasz van a kamionnal!");
            System.out.println(""+ex);
        }
        }
        //Megzézzük, hogy üres-e az adatbázis, és létezik-e az adott adattábla       
        try {
        dbmd = conn.getMetaData();
        } 
        catch (SQLException ex) {
            System.out.println("Gebasz van a metaadatokkal!");
            System.out.println(""+ex);
        }
        
        try {
        ResultSet rs = dbmd.getTables(null, "APP", "CONTACTS", null);
        if (!rs.next()){
        createStatement.execute("create table contacts (id INT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),lastname varchar(20), firstname varchar(20), email varchar(30))");
            }
        } catch (SQLException ex) {
        System.out.println("Gebasz van az adattábla létrehozásával!");
        System.out.println(""+ex);
        }
        }
    
    //Userek hozzáadása
    public void addContact(Person person){
    try {
    String sql = "insert into contacts (lastname, firstname, email)values (?,?,?)"; //itt a beérkező paramétereket explicit meg kell adni az syl-nek, mivel a konstruktor negyedik ID paraméterét nem adjuk, meg, és különben sírna, mert nem tudná eldőnteni, hogy mik ezek a bejövő stringek...
    PreparedStatement preparedStatement = conn.prepareStatement (sql);
    preparedStatement.setString(1, person.getLastName() );
    preparedStatement.setString(2, person.getFirstName() );
    preparedStatement.setString(3, person.getEmail() );
    preparedStatement.execute(); 
    
    } catch (SQLException ex) {
    System.out.println("Gebasz van a kapcsolatok hozzáadásával!");
    System.out.println(""+ex);
    }
    }
   
    //Adatok módosítása
    public void updateContact(Person person){
    try {
    String sql = "update contacts set lastname = ?, firstname = ?, email = ? where id = ?";
    PreparedStatement preparedStatement = conn.prepareStatement (sql);
    preparedStatement.setString(1, person.getLastName() );
    preparedStatement.setString(2, person.getFirstName() );
    preparedStatement.setString(3, person.getEmail() );
    preparedStatement.setInt(4, Integer.parseInt(person.getId()));
    preparedStatement.execute(); 
    } catch (SQLException ex) {
    System.out.println("Gebasz van a kapcsolatok hozzáadásával!");
    System.out.println(""+ex);
    }
    }
    

            public ArrayList<Person> getAllContacts(){
            String sql= "select * from contacts";
            ArrayList<Person> contacts = null;
            try {
            ResultSet rs = createStatement.executeQuery(sql);
            contacts = new ArrayList<>();
            while (rs.next()){
                Person actualPerson = new Person (rs.getInt("id"),rs.getString("lastname"), rs.getString("firstname"),rs.getString("email"));
            contacts.add(actualPerson);
            }
            } catch (SQLException ex) {
            System.out.println("Gebasz van a kapcsolatok kilistázásával!");
            System.out.println(""+ex);
            }
            return contacts;
            }
            
            
            
}
