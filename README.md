# Readable Thrift

Readable Thrift makes binary Thrift protocol messages easy to work with by converting them
to and from a human-friendly format. This makes manual analysis of and tampering with binary format Thrift
messages just as easy as working with plaintext protocols like HTTP.

The library is implemented in Java, enabling integration with extensions for popular web
application testing tools that run on the JVM, such as Burp and ZAP. All one has to do to get an analysis or tampering
tool up and running is parse out the appropriate bytes and pass them to the codec.


## Library usage

The `readablethrift` library is a Gradle-based Maven package that can be
installed included as a dependency in other projects. It exposes four simple
methods for encoding and decoding, in raw binary or Base64-encoded form.

***Note:*** Only the standard binary format protocol is currently supported.
Compact binary and JSON (different from the readable JSON produced by the codec)
are not yet supported.


### Decoding Messages - Thrift binary to JSON

* The `ThriftCodec::decode` method creates a descriptive `org.json.JSONObject`
  instance directly from a raw byte buffer that contains a binary Thrift message.
* The `ThriftCodec::decodeB64String` static method will decode a Base64 string
  and then call `decode` on it.


### Encoding Messages - JSON to Thrift binary

* The `ThriftCodec::encode` method converts a descriptive `org.json.JSONObject` instance to a raw
  byte buffer containing the equivalent Thrift binary message.
* The `ThriftCodec::b64encodeJson` static method will convert a JSON object
  to Thrift binary format and Base64 encode the result.

### Extensibility

The `ThriftCodec` class is designed to be extensible so that it can handle
modifications or extensions to the Thrift binary protocol format, such as extra
binary format headers or encapsulation.


## Command line usage

To use the interactive command line interface included in the library,
use the default Gradle task to build a JAR and then run it with
`java -jar`:

```console
$ ./gradlew

BUILD SUCCESSFUL in 1s
2 actionable tasks: 2 executed
$ java -jar build/libs/readablethrift-0.1.0-all.jar 
Enter Base64 encoded message or JSON object (or q to quit): 
```


## Demonstration

To demonstrate how the human-readable JSON will look, let's take an example Base64-encoded binary blob
extracted from some application traffic:

```console
$ base64 -D | hexdump
gAEAAQAAAAVsb2dpbgAAAAAMAAALAAAAAAAIY29vbHVzZXILAAEAAAALcGFzc3dvcmQxMjMAAA==
00000000: 8001 0001 0000 0005 6c6f 6769 6e00 0000  ........login...
00000010: 000c 0000 0b00 0000 0000 0863 6f6f 6c75  ...........coolu
00000020: 7365 720b 0001 0000 000b 7061 7373 776f  ser.......passwo
00000030: 7264 3132 3300 00                        rd123..
```

The "login", username, and password strings hint that this is an authentication request.
If one wanted to tamper with this message on the fly, they would have to look up the internal details of
the Thrift protocol binary format parser, figure out the structure of the message with a hex editor,
and then manually update each field. In the case of the username and password strings, it would also be necessary to
update the prefixed length values in addition to the strings themselves. This would be a tedious, error prone,
and time consuming process. Readable Thrift makes this process simpler; here is the same message when passed to it:

```
Enter Base64 encoded message or JSON object (or q to quit): gAEAAQAAAAVsb2dpbgAAAAAMAAALAAAAAAAIY29vbHVzZXILAAEAAAALcGFzc3dvcmQxMjMAAA==
```

```json
{"message": {
    "name": "login",
    "type": "call",
    "fields": [{
        "id": 0,
        "type": "struct",
        "value": [
            {
                "id": 0,
                "type": "string",
                "value": "cooluser"
            },
            {
                "id": 1,
                "type": "string",
                "value": "password123"
            }
        ]
    }],
    "seqid": 0
}}
```

Now we can easily verify that the message describes a call to the "login" method, with a struct
containing two string values as the argument. Updating the string values is as simple as writing
editing the `value` JSON fields and passing the new JSON text back to the codec.
All length prefixes and other details of the binary format will be handled automatically.

It is also possible to add new fields to test out possible optional parameters.
Maybe there is an admin or debug boolean flag? Or a list of permission IDs?
Describe a new field with an `id`, `type`, and `value`, and then add it to
the `fields` array:

```
Enter Base64 encoded message or JSON object (or q to quit): {"message": {
    "name": "login",
    "type": "call",
    "fields": [{
        "id": 0,
        "type": "struct",
        "value": [
            {
                "id": 0,
                "type": "string",
                "value": "differentuser"
            },
            {
                "id": 1,
                "type": "string",
                "value": "differentpassword!"
            }
        ]
    },
    {"id": 1, "type": "bool", "value": true},
    {"id": 2, "type": "list", "value":
        {
            "elemType": "i32",
            "list": [1, 2, 3]
        }
    },
    {"id": 3, "type": "map", "value":
        {
            "keyType": "string",
            "valueType": "string",
            "map": {
                "dog": "bark",
                "cat": "meow"
            }
        }
    }],
    "seqid": 0
}}
```

```
gAEAAQAAAAVsb2dpbgAAAAAMAAALAAAAAAANZGlmZmVyZW50dXNlcgsAAQAAABJkaWZmZXJlbnRwYXNzd29yZCEAAgABAQ8AAggAAAADAAAAAQAAAAIAAAADDQADCwsAAAACAAAAA2NhdAAAAARtZW93AAAAA2RvZwAAAARiYXJrAA==
```

Below is the updated message converted back to the Thrift binary protocol:

```console
$ base64 -D | hexdump
gAEAAQAAAAVsb2dpbgAAAAAMAAALAAAAAAANZGlmZmVyZW50dXNlcgsAAQAAABJkaWZmZXJlbnRwYXNzd29yZCEAAgABAQ8AAggAAAADAAAAAQAAAAIAAAADDQADCwsAAAACAAAAA2NhdAAAAARtZW93AAAAA2RvZwAAAARiYXJrAA==
00000000: 8001 0001 0000 0005 6c6f 6769 6e00 0000  ........login...
00000010: 000c 0000 0b00 0000 0000 0d64 6966 6665  ...........diffe
00000020: 7265 6e74 7573 6572 0b00 0100 0000 1264  rentuser.......d
00000030: 6966 6665 7265 6e74 7061 7373 776f 7264  ifferentpassword
00000040: 2100 0200 0101 0f00 0208 0000 0003 0000  !...............
00000050: 0001 0000 0002 0000 0003 0d00 030b 0b00  ................
00000060: 0000 0200 0000 0363 6174 0000 0004 6d65  .......cat....me
00000070: 6f77 0000 0003 646f 6700 0000 0462 6172  ow....dog....bar
00000080: 6b00
```
