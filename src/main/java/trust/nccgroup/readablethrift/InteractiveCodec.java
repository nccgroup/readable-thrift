package trust.nccgroup.readablethrift;

import org.apache.thrift.TException;
import org.json.JSONObject;

import java.util.Scanner;

public class InteractiveCodec {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter Base64 encoded message or JSON object (or q to quit): ");

            if (!scanner.hasNextLine()) {
                scanner = new Scanner(System.in);
            }

            String input = scanner.nextLine();

            if (input.equals("q")) {
                break;
            } else if (input.startsWith("{")) {
                StringBuilder inputJsonBuilder = new StringBuilder();
                inputJsonBuilder.append(input);

                while (scanner.hasNextLine()) {
                    inputJsonBuilder.append(scanner.nextLine());
                }

                String concatted = inputJsonBuilder.toString();

                JSONObject inputJson = new JSONObject(concatted);

                try {
                    System.out.println(ThriftCodec.b64encodeJson(inputJson));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONObject result = ThriftCodec.decodeB64String(input);
                    System.out.println(result.toString(4));
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
        }

        scanner.close();
    }

}
