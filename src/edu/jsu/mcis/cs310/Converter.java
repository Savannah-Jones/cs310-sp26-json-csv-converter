package edu.jsu.mcis.cs310;

import java.io.*;
import java.util.*;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        
        String result = "{}"; // default return value; replace later!
        
        try {
           //csv reader, write json, serialize
          CSVReader reader = new CSVReader(new StringReader(csvString));
          List<String[]> contents = reader.readAll();
        //row 0 = header, 1...n episodes

          String[] ColHeadings = contents.get(0);
          List<String> prodnum = new ArrayList<>();
          List<Object[]> data = new ArrayList<>();

//            //split csv data

          JsonObject obj = new JsonObject();
          int rowcount = 0;
          for(String[] row : contents){
                if(rowcount >= 1){
                    Object[] episode = new Object[6];
                    prodnum.add(row[0]);
                    for (int column = 1; column <= 6; column++){
                        
                        if(column == 2 || column == 3){ //season-episode are ints
                            episode[column-1] = Integer.parseInt(row[column]);
                        }
                        else{
                            episode[column-1] = row[column];
                        }
                    }
                    data.add(episode);
                }
            rowcount++;
          }
          
          //assemble json object
          obj.put("ProdNums", prodnum);
          obj.put("ColHeadings", ColHeadings);
          obj.put("Data", data);
          
          
          result = Jsoner.serialize(obj);            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {          
            //"ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
            //json "ColHeadings" needs to become row 0
            //then json "Prodnum" + "Data" become rows 1..n
            //*remember to reInt season/episode
           

            JsonObject obj = (JsonObject) Jsoner.deserialize(jsonString);

            JsonArray prodnums = (JsonArray) obj.get("ProdNums");
            JsonArray ColHeadings = (JsonArray) obj.get("ColHeadings");
            JsonArray data = (JsonArray) obj.get("Data");
            StringWriter stringWriter = new StringWriter();
            CSVWriter writer = new CSVWriter(stringWriter);

            writer.writeNext(ColHeadings.stream().map(Object::toString).toArray(String[]::new));//row 0, converts JsonArray into String[] for writer
            
            
            for(int i = 0; i < prodnums.size(); i++){ //every prodnum corresponds to episode so prodnums.size()
                Object[] CSVrow = new Object[7];
             
                CSVrow[0] = prodnums.get(i);//"ProdNum"
                
                JsonArray episode = (JsonArray) data.get(i);//"Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
              
                for(int j = 0; j < episode.size(); j++){
                  if(j == 2){
                      int episodenum = Integer.parseInt(episode.get(j).toString()); // get episodenum as int
                      CSVrow[j + 1] = String.format("%02d", episodenum); //format it for assignment
                  }
                  else{
                      CSVrow[j + 1]/*move row 1 over to account for prodnum*/ = episode.get(j); 
                  }
                }
                writer.writeNext(Arrays.stream(CSVrow).map(Object::toString).toArray(String[]::new)); //write row
            }
            writer.close();

            result = stringWriter.toString();
           
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
