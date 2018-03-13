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
import org.apache.thrift.TException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import trust.nccgroup.readablethrift.ThriftCodec;

import static org.junit.Assert.fail;

public class ThriftDecoderTest {

    /** Test decoding a message from Base64'd binary protocol into a JSON object. */
    private static void testDecodeB64Message (String message) {
        try {
            JSONObject decoded = ThriftCodec.decodeB64String(message);
            System.out.println(decoded.toString(4) + "\n");
        } catch (TException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }


    /** Test that a decoded message remains the same when re-encoded. */
    private static void testDecodeEncode(String originalString) {
        try {
            JSONObject decoded = ThriftCodec.decodeB64String(originalString);
            String reEncoded = ThriftCodec.b64encodeJson(decoded);
            JSONObject reDecoded = ThriftCodec.decodeB64String(reEncoded);

            JSONAssert.assertEquals(decoded, reDecoded, true);
        } catch (TException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testDecodeStrings() {
        // has a struct list in it
        testDecodeB64Message("gAEAAQAAAAZ0ZXN0IzEAAAAACwABAAAAIDAxMjM0NTY3ODlhYmNkZWYwMTIzNDU2Nzg5YWJjZGVmCwACAAAAIDAxMjM0NTY3ODlhYmNkZWYwMTIzNDU2Nzg5YWJjZGVmCgADAAABWLIzi9kPAAQMAAAAAAsABQAAAEJ0aGlzIGlzIGEgdGVzdCBzdHJpbmcgdGhpcyBpcyBhIHRlc3Qgc3RyaW5nIHRoaXMgaXMgYSB0ZXN0IHN0cmluZyALAAYAAAAkYW5vdGhlciB0ZXN0IHN0cmluZyBhbm90aGVyIHRlc3Qgc3RyAA==");
    }

    @Test
    public void testDecodeQuery() {
        // has a few fields and a query string
        testDecodeB64Message("gAEAAQAAAAVxdWVyeQAAAAAMAAICAAEBAAwAAwsAAgAAABRTRUxFQ1QgKiBGUk9NIHVzZXJzOwAMAAQIAAEAAAACAAA=");
    }

    @Test
    public void testDecodeNoFields() {
        // no fields, just a call with a name
        testDecodeB64Message("gAEAAQAAAAhub0ZpZWxkcwAAAAAA");
    }

    @Test
    public void testDecodeListsAndStructs() {
        // This message has a map in it
        testDecodeB64Message("gAEAAQAAAAVxdWVyeQAAAAAMAAECAAEBAAwAAgwAAwsAAQAAAAR0ZXN0DAACCwADAAAAC3N0cnVjdCB0ZXN0AA0AAwsMAAAAAA8ABAwAAAACCwABAAAADmFub3RoZXIgc3RyaW5nDwAFCwAAAAMAAAAEbGlzdAAAAAJvZgAAAAdzdHJpbmdzAAsAAQAAAAxtb3JlIHN0cmluZ3MLAAIAAAAPbG90cyBvZiBzdHJpbmdzDwAFCwAAAAMAAAAHYW5vdGhlcgAAAAZuZXN0ZWQAAAAEbGlzdAAAAAwABAgAAQAABTkAAA==");
    }

    /** Test that a decoded message remains the same when re-encoded. */
    @Test
    public void testDecodeEncode() {
        String originalString = "gAEAAQAAAAVxdWVyeQAAAAAMAAECAAEBAAwAAgwAAwsAAQAAAAR0ZXN0DAACCwADAAAAC3N0cnVjdCB0ZXN0AA0AAwsMAAAAAA8ABAwAAAACCwABAAAADmFub3RoZXIgc3RyaW5nDwAFCwAAAAMAAAAEbGlzdAAAAAJvZgAAAAdzdHJpbmdzAAsAAQAAAAxtb3JlIHN0cmluZ3MLAAIAAAAPbG90cyBvZiBzdHJpbmdzDwAFCwAAAAMAAAAHYW5vdGhlcgAAAAZuZXN0ZWQAAAAEbGlzdAAAAAwABAgAAQAABTkAAA==";
        testDecodeEncode(originalString);
    }

}
