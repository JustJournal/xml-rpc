package com.justjournal.xmlrpc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

class XmlRpcValueTest {

  private XmlRpcValue xmlRpcValue;

  @BeforeEach
  void setUp() {
    xmlRpcValue = new XmlRpcValue();
  }

  @Test
  void testStringValue() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.STRING);
    xmlRpcValue.processCharacterData("Hello, World!");
    assertEquals("Hello, World!", xmlRpcValue.value);
  }

  @Test
  void testIntValue() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.INT);
    xmlRpcValue.processCharacterData("42");
    assertEquals(42, xmlRpcValue.value);
  }

  @Test
  void testLongValue() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.I8);
    xmlRpcValue.processCharacterData("9223372036854775807");
    assertEquals(9223372036854775807L, xmlRpcValue.value);
  }

  @Test
  void testBooleanValue() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.BOOLEAN);
    xmlRpcValue.processCharacterData("1");
    assertEquals(Boolean.TRUE, xmlRpcValue.value);

    xmlRpcValue.processCharacterData("0");
    assertEquals(Boolean.FALSE, xmlRpcValue.value);
  }

  @Test
  void testDoubleValue() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.DOUBLE);
    xmlRpcValue.processCharacterData("3.14159");
    assertEquals(3.14159, xmlRpcValue.value);
  }

  @Test
  void testInvalidDateValue() {
    xmlRpcValue.setType(XmlRpcParser.DATE);
    Assertions.assertThrows(XmlRpcException.class, () -> {
      xmlRpcValue.processCharacterData("invalid-date");
    });
  }

  @Test
  void testBase64Value() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.BASE64);
    xmlRpcValue.processCharacterData("SGVsbG8sIFdvcmxkIQ==");
    assertInstanceOf(byte[].class, xmlRpcValue.value);
    assertEquals("Hello, World!", new String((byte[]) xmlRpcValue.value));
  }

  @Test
  void testArrayValue() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.ARRAY);
    assertInstanceOf(XmlRpcArray.class, xmlRpcValue.value);

    XmlRpcValue childValue = new XmlRpcValue();
    childValue.setType(XmlRpcParser.STRING);
    childValue.processCharacterData("Test");

    xmlRpcValue.addChildValue(childValue);

    XmlRpcArray array = (XmlRpcArray) xmlRpcValue.value;
    assertEquals(1, array.size());
    assertEquals("Test", array.get(0));
  }

  @Test
  void testStructValue() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.STRUCT);
    assertInstanceOf(XmlRpcStruct.class, xmlRpcValue.value);

    xmlRpcValue.processCharacterData("key");

    XmlRpcValue childValue = new XmlRpcValue();
    childValue.setType(XmlRpcParser.STRING);
    childValue.processCharacterData("value");

    xmlRpcValue.addChildValue(childValue);

    XmlRpcStruct struct = (XmlRpcStruct) xmlRpcValue.value;
    assertEquals(1, struct.size());
    assertEquals("value", struct.get("key"));
  }

  @Test
  void testAddChildValueToNonCompositeType() throws XmlRpcException {
    xmlRpcValue.setType(XmlRpcParser.STRING);
    XmlRpcValue childValue = new XmlRpcValue();

    Assertions.assertThrows(
        XmlRpcException.class,
        () -> {
          xmlRpcValue.addChildValue(childValue);
        });
  }
}
