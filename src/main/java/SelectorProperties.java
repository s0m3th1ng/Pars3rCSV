import lombok.Data;

@Data
public class SelectorProperties {

    private final String[] header;
    private final char separator;
    private final String outputFilename;

}
