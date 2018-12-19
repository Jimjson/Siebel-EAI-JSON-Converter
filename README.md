Siebel-EAI-JSON-Converter

=========================
EAI JSON Converter for Siebel

EAI JSON Converter is a custom Siebel Business service which is built using EAI Java Business Service and GSON API(https://code.google.com/p/google-gson/) to convert Siebel Property set to JSON and vice versa.

Service Provides two methods:

1. PropSetToJSON
2. JSONToPropSet

Dependencies:
gson-2.2.4.jar
Siebel.jar

Developed & tested on JDK 1.5

How to update the code : https://github.com/Jimjson/Siebel-EAI-JSON-Converter/wiki/How-to-make-changes%

Examples of use: http://howtosiebel.blogspot.com/2013/12/eai-json-converter.html
Example in WF EAI JSON Converter Tests WF.xml read an JSON from file c:\000.json, execute JSONToPropSet and create sif with IO. After Import from archive and compilation IO 000-io can change parameter IsCreateIO and in next simulation of WF read json to new IO over XML.
