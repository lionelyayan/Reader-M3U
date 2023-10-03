# Reader-M3U
Reader M3U Library is a lightweight and easy-to-use Java library for parsing and working with M3U playlist files. M3U is a common format used for creating playlists of audio and video files. This library allows you to programmatically read and manipulate M3U playlist files in your Java applications.

Features:
1. Read from URL
2. Read from File
3. Read from syntax written by you yourself

## Add dependency
`settings.gradle`
```groovy
maven {
    url "https://jitpack.io"
}
```
`build.gradle`
```groovy
dependencies {
    implementation 'com.github.lionelyayan:Reader-M3U:1.0.0'
}
```

## Usage
```
import com.devertindo.reader_m3u.M3UItems;
import com.devertindo.reader_m3u.ReaderM3U;
```
1. From URL
```groovy
String url_m3u = ".....";
String user_agent = "....."; //leave blank if not present

ReaderM3U.readFromUrl(url_m3u, user_agent, new ReaderM3U.OnReadListener() {
    @Override
    public void onRead(List<M3UItems> items) {
        //list of m3u items
    }

    @Override
    public void onError(String err) {
        Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
    }
});
```

2. From File
```groovy
ReaderM3U.readFromFile(context, uri, new ReaderM3U.OnReadListener() {
    @Override
    public void onRead(List<M3UItems> items) {
        //list of m3u items
    }

    @Override
    public void onError(String err) {
        Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
    }
});
```

3. From syntax
```groovy
ReaderM3U.readFromInput(strValue, new ReaderM3U.OnReadListener() {
    @Override
    public void onRead(List<M3UItems> items) {
        //list of m3u items
    }

    @Override
    public void onError(String err) {
        Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
    }
});
```
<br>`available properties`
- ext_http
- manifest_type
- http_user_agent
- http_referrer
- license_type
- license_key
- tvg_id
- tvg_logo
- group_title
- title
- url

## Contributions
Contributions and bug reports are welcome! If you encounter any issues or have suggestions for improvements, please open an issue or submit a pull request.

## License
Licensed under the [Apache License 2.0][2]

	Copyright (C) 2023 lionelyayan

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.



[1]: https://github.com/lionelyayan/Reader-M3U
[2]: http://www.apache.org/licenses/LICENSE-2.0
