/*
    Copyright (c) 2005 Redstone Handelsbolag

    This library is free software; you can redistribute it and/or modify it under the terms
    of the GNU Lesser General Public License as published by the Free Software Foundation;
    either version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License along with this
    library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
    Boston, MA  02111-1307  USA
*/

package com.justjournal.xmlrpc.handlers;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Handler for the XML-RPC validation suite. This suite is used to verify the implementation using
 * the verification service at <a href="http://www.xmlrpc.com">...</a>.
 *
 * @author Greger Olsson
 */
@Slf4j
public class ValidationHandler extends ReflectiveInvocationHandler {

  private static final String CURLY = "curly";

  /**
   * This handler takes a single parameter, an array of structs, each of which contains at least
   * three elements named moe, larry and curly, all &lt;i4&gt;s. Your handler must add all the
   * struct elements named curly and return the result.
   * @param structs An array of structs.
   */
  public int arrayOfStructsTest(List<Object> structs) {
    int result = 0;

    for (Object o : structs) {
      Map<String,Object> struct = (Map<String,Object>) o;
      result += (Integer) struct.get(CURLY);
    }

    return result;
  }

  /**
   * This handler takes a single parameter, a string, that contains any number of predefined
   * entities, namely &lt;, &gt;, &amp;, ' and &quot;. Your handler must return a struct that
   * contains five fields, all numbers: ctLeftAngleBrackets, ctRightAngleBrackets, ctAmpersands,
   * ctApostrophes, ctQuotes. To validate, the numbers must be correct.
   */
  public Map<String, Integer> countTheEntities(String str) {
    int ctLeftAngleBrackets = 0;
    int ctRightAngleBrackets = 0;
    int ctAmpersands = 0;
    int ctApostrophes = 0;
    int ctQuotes = 0;

    for (int i = 0; i < str.length(); ++i) {
      switch (str.charAt(i)) {
        case '<':
          ++ctLeftAngleBrackets;
          break;
        case '>':
          ++ctRightAngleBrackets;
          break;
        case '&':
          ++ctAmpersands;
          break;
        case '\'':
          ++ctApostrophes;
          break;
        case '\"':
          ++ctQuotes;
          break;
        default:
      }
    }

    Map<String, Integer> result = new HashMap<>();

    result.put("ctLeftAngleBrackets", ctLeftAngleBrackets);
    result.put("ctRightAngleBrackets", ctRightAngleBrackets);
    result.put("ctAmpersands", ctAmpersands);
    result.put("ctApostrophes", ctApostrophes);
    result.put("ctQuotes", ctQuotes);

    return result;
  }

  /**
   * This handler takes a single parameter, a struct, containing at least three elements named moe,
   * larry and curly, all &lt;i4&gt;s. Your handler must add the three numbers and return the
   * result.
   *
   * @param struct The struct containing the numbers
   */
  public int easyStructTest(Map<String,Object> struct) {
    int result = 0;

    result += (Integer) struct.get("moe");
    result += (Integer) struct.get("larry");
    result += (Integer) struct.get(CURLY);

    return result;
  }

  /**
   * This handler takes a single parameter, a struct. Your handler must return the struct.
   *
   * @param struct The struct to echo
   */
  public Map echoStructTest(Map struct) {
    return struct;
  }

  /**
   * This handler takes six parameters, and returns an array containing all the parameters.
   *
   * @param number The first parameter
   * @param bool The second parameter
   * @param string The third parameter
   * @param dbl The fourth parameter
   * @param dateTime The fifth parameter
   * @param bytes The sixth parameter
   * @return An array containing all the parameters
   */
  public List<Object> manyTypesTest(
      int number, boolean bool, String string, double dbl, Date dateTime, byte[] bytes) {
    List<Object> result = new ArrayList<>(6);

    result.add(number);
    result.add(bool);
    result.add(string);
    result.add(dbl);
    result.add(dateTime);
    result.add(bytes);

    return result;
  }

  /**
   * This handler takes a single parameter, which is an array containing between 100 and 200
   * elements. Each of the items is a string, your handler must return a string containing the
   * concatenated text of the first and last elements.
   *
   * @param strings The array containing the strings
   * @return A string containing the concatenated text of the first and last elements
   */
  public String moderateSizeArrayCheck(List<String> strings) {
    return (strings.get(0)) + (strings.get(strings.size() - 1));
  }

  /**
   * This handler takes a single parameter, a struct, that models a daily calendar. At the top
   * level, there is one struct for each year. Each year is broken down into months, and months into
   * days. Most of the days are empty in the struct you receive, but the entry for April 1, 2000
   * contains a least three elements named moe, larry and curly, all &lt;i4&gt;s. Your handler must
   * add the three numbers and return the result.
   *
   * @param struct The struct containing the daily calendar
   * @return The result of adding the three numbers in April 1, 2000
   */
  public int nestedStructTest(Map<String, Object> struct) {
    int result = 0;

    try {
      struct = (Map) struct.get("2000");
      struct = (Map) struct.get("04");
      struct = (Map) struct.get("01");

      result += (Integer) struct.get("moe");
      result += (Integer) struct.get("larry");
      result += (Integer) struct.get(CURLY);
    } catch (Exception e) {
      log.error("Error parsing nested struct: {}", e.getMessage(), e);
    }

    return result;
  }

  /**
   * This handler takes one parameter, and returns a struct containing three elements, times10,
   * times100 and times1000, the result of multiplying the number by 10, 100 and 1000.
   *
   * @param number The number to multiply
   * @return A struct containing the multiplication results 10, 100 and 1000 times the input number
   */
  public Map<String, Integer> simpleStructReturnTest(int number) {
    Map<String, Integer> result = new HashMap<>();

    result.put("times10", number * 10);
    result.put("times100", number * 100);
    result.put("times1000", number * 1000);

    return result;
  }
}
