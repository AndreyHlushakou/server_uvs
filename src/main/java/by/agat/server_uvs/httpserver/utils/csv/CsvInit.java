package by.agat.server_uvs.httpserver.utils.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import static by.agat.server_uvs.httpserver.utils.csv.DataFromCsv.mazParamsList;

@Slf4j
@Component
public class CsvInit {

    @Value("${path.maz.csv}")
    private String path;


    @PostConstruct
    public void init() {
        File file;
        try {
            URL url = this.getClass().getResource(path);
            URI FILE_PATH = Objects.requireNonNull(url).toURI();
             file = new File(FILE_PATH);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        mazParamsList = new ArrayList<>();
        try (
                FileReader fileReader = new FileReader(file);
                CSVReader csvReader = new CSVReaderBuilder(fileReader).build();
        ){
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String[] arrStr = nextLine[0].split(";");
                long CAN_ID  = Long.parseLong(  arrStr[0], 16);
                int byte_    = Integer.parseInt(arrStr[1], 10);
                int bit      = Integer.parseInt(arrStr[2], 10);
                int size     = Integer.parseInt(arrStr[3], 10);
                int out_byte = Integer.parseInt(arrStr[4], 10);
                int out_bit  = Integer.parseInt(arrStr[5], 10);

                mazParamsList.add(
                        new MazParams()
                                .setCAN_ID(CAN_ID)
                                .setByte_(byte_)
                                .setBit(bit)
                                .setSize(size)
                                .setOut_byte(out_byte)
                                .setOut_bit(out_bit)
                );
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

    }

}
