/*
   Copyright 2018 NCC Group

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
