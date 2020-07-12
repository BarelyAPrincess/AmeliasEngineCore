/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.extra;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.IDN;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.amelia.lang.APINotice;

public class UtilityStrings
{
	public static final Map<String, String[]> CHARS_MAP;
	private static final Pattern SIMPLE_REGEX_PATTERN_CHARS = Pattern.compile( "^[\\w\\d\\s\\n\\t\\r\\\\.,*?+{}|]*$" );

	static
	{
		Map<String, String[]> chars = new HashMap<>();

		chars.put( "0", new String[] {"°", "₀", "۰"} );
		chars.put( "1", new String[] {"¹", "₁", "۱"} );
		chars.put( "2", new String[] {"²", "₂", "۲"} );
		chars.put( "3", new String[] {"³", "₃", "۳"} );
		chars.put( "4", new String[] {"⁴", "₄", "۴", "٤"} );
		chars.put( "5", new String[] {"⁵", "₅", "۵", "٥"} );
		chars.put( "6", new String[] {"⁶", "₆", "۶", "٦"} );
		chars.put( "7", new String[] {"⁷", "₇", "۷"} );
		chars.put( "8", new String[] {"⁸", "₈", "۸"} );
		chars.put( "9", new String[] {"⁹", "₉", "۹"} );
		chars.put( "a", new String[] {"à", "á", "ả", "ã", "ạ", "ă", "ắ", "ằ", "ẳ", "ẵ", "ặ", "â", "ấ", "ầ", "ẩ", "ẫ", "ậ", "ā", "ą", "å", "α", "ά", "ἀ", "ἁ", "ἂ", "ἃ", "ἄ", "ἅ", "ἆ", "ἇ", "ᾀ", "ᾁ", "ᾂ", "ᾃ", "ᾄ", "ᾅ", "ᾆ", "ᾇ", "ὰ", "ά", "ᾰ", "ᾱ", "ᾲ", "ᾳ", "ᾴ", "ᾶ", "ᾷ", "а", "أ", "အ", "ာ", "ါ", "ǻ", "ǎ", "ª", "ა", "अ", "ا"} );
		chars.put( "b", new String[] {"б", "β", "Ъ", "Ь", "ب", "ဗ", "ბ"} );
		chars.put( "c", new String[] {"ç", "ć", "č", "ĉ", "ċ"} );
		chars.put( "d", new String[] {"ď", "ð", "đ", "ƌ", "ȡ", "ɖ", "ɗ", "ᵭ", "ᶁ", "ᶑ", "д", "δ", "د", "ض", "ဍ", "ဒ", "დ"} );
		chars.put( "e", new String[] {"é", "è", "ẻ", "ẽ", "ẹ", "ê", "ế", "ề", "ể", "ễ", "ệ", "ë", "ē", "ę", "ě", "ĕ", "ė", "ε", "έ", "ἐ", "ἑ", "ἒ", "ἓ", "ἔ", "ἕ", "ὲ", "έ", "е", "ё", "э", "є", "ə", "ဧ", "ေ", "ဲ", "ე", "ए", "إ", "ئ"} );
		chars.put( "f", new String[] {"ф", "φ", "ف", "ƒ", "ფ"} );
		chars.put( "g", new String[] {"ĝ", "ğ", "ġ", "ģ", "г", "ґ", "γ", "ဂ", "გ", "گ"} );
		chars.put( "h", new String[] {"ĥ", "ħ", "η", "ή", "ح", "ه", "ဟ", "ှ", "ჰ"} );
		chars.put( "i", new String[] {"í", "ì", "ỉ", "ĩ", "ị", "î", "ï", "ī", "ĭ", "į", "ı", "ι", "ί", "ϊ", "ΐ", "ἰ", "ἱ", "ἲ", "ἳ", "ἴ", "ἵ", "ἶ", "ἷ", "ὶ", "ί", "ῐ", "ῑ", "ῒ", "ΐ", "ῖ", "ῗ", "і", "ї", "и", "ဣ", "ိ", "ီ", "ည်", "ǐ", "ი", "इ"} );
		chars.put( "j", new String[] {"ĵ", "ј", "Ј", "ჯ", "ج"} );
		chars.put( "k", new String[] {"ķ", "ĸ", "к", "κ", "Ķ", "ق", "ك", "က", "კ", "ქ", "ک"} );
		chars.put( "l", new String[] {"ł", "ľ", "ĺ", "ļ", "ŀ", "л", "λ", "ل", "လ", "ლ"} );
		chars.put( "m", new String[] {"м", "μ", "م", "မ", "მ"} );
		chars.put( "n", new String[] {"ñ", "ń", "ň", "ņ", "ŉ", "ŋ", "ν", "н", "ن", "န", "ნ"} );
		chars.put( "o", new String[] {"ó", "ò", "ỏ", "õ", "ọ", "ô", "ố", "ồ", "ổ", "ỗ", "ộ", "ơ", "ớ", "ờ", "ở", "ỡ", "ợ", "ø", "ō", "ő", "ŏ", "ο", "ὀ", "ὁ", "ὂ", "ὃ", "ὄ", "ὅ", "ὸ", "ό", "о", "و", "θ", "'ို", "ǒ", "ǿ", "º", "ო", "ओ"} );
		chars.put( "p", new String[] {"п", "π", "ပ", "პ", "پ"} );
		chars.put( "q", new String[] {"ყ"} );
		chars.put( "r", new String[] {"ŕ", "ř", "ŗ", "р", "ρ", "ر", "რ"} );
		chars.put( "s", new String[] {"ś", "š", "ş", "с", "σ", "ș", "ς", "س", "ص", "စ", "ſ", "ს"} );
		chars.put( "t", new String[] {"ť", "ţ", "т", "τ", "ț", "ت", "ط", "ဋ", "တ", "ŧ", "თ", "ტ"} );
		chars.put( "u", new String[] {"ú", "ù", "ủ", "ũ", "ụ", "ư", "ứ", "ừ", "ử", "ữ", "ự", "û", "ū", "ů", "ű", "ŭ", "ų", "µ", "у", "ဉ", "ု", "ူ", "ǔ", "ǖ", "ǘ", "ǚ", "ǜ", "უ", "उ"} );
		chars.put( "v", new String[] {"в", "ვ", "ϐ"} );
		chars.put( "w", new String[] {"ŵ", "ω", "ώ", "ဝ", "ွ"} );
		chars.put( "x", new String[] {"χ", "ξ"} );
		chars.put( "y", new String[] {"ý", "ỳ", "ỷ", "ỹ", "ỵ", "ÿ", "ŷ", "й", "ы", "υ", "ϋ", "ύ", "ΰ", "ي", "ယ"} );
		chars.put( "z", new String[] {"ź", "ž", "ż", "з", "ζ", "ز", "ဇ", "ზ"} );
		chars.put( "aa", new String[] {"ع", "आ", "آ"} );
		chars.put( "ae", new String[] {"ä", "æ", "ǽ"} );
		chars.put( "ai", new String[] {"ऐ"} );
		chars.put( "at", new String[] {"@"} );
		chars.put( "ch", new String[] {"ч", "ჩ", "ჭ", "چ"} );
		chars.put( "dj", new String[] {"ђ", "đ"} );
		chars.put( "dz", new String[] {"џ", "ძ"} );
		chars.put( "ei", new String[] {"ऍ"} );
		chars.put( "gh", new String[] {"غ", "ღ"} );
		chars.put( "ii", new String[] {"ई"} );
		chars.put( "ij", new String[] {"ĳ"} );
		chars.put( "kh", new String[] {"х", "خ", "ხ"} );
		chars.put( "lj", new String[] {"љ"} );
		chars.put( "nj", new String[] {"њ"} );
		chars.put( "oe", new String[] {"ö", "œ", "ؤ"} );
		chars.put( "oi", new String[] {"ऑ"} );
		chars.put( "oii", new String[] {"ऒ"} );
		chars.put( "ps", new String[] {"ψ"} );
		chars.put( "sh", new String[] {"ш", "შ", "ش"} );
		chars.put( "shch", new String[] {"щ"} );
		chars.put( "ss", new String[] {"ß"} );
		chars.put( "sx", new String[] {"ŝ"} );
		chars.put( "th", new String[] {"þ", "ϑ", "ث", "ذ", "ظ"} );
		chars.put( "ts", new String[] {"ц", "ც", "წ"} );
		chars.put( "ue", new String[] {"ü"} );
		chars.put( "uu", new String[] {"ऊ"} );
		chars.put( "ya", new String[] {"я"} );
		chars.put( "yu", new String[] {"ю"} );
		chars.put( "zh", new String[] {"ж", "ჟ", "ژ"} );
		chars.put( "(c)", new String[] {"©"} );
		chars.put( "A", new String[] {"Á", "À", "Ả", "Ã", "Ạ", "Ă", "Ắ", "Ằ", "Ẳ", "Ẵ", "Ặ", "Â", "Ấ", "Ầ", "Ẩ", "Ẫ", "Ậ", "Å", "Ā", "Ą", "Α", "Ά", "Ἀ", "Ἁ", "Ἂ", "Ἃ", "Ἄ", "Ἅ", "Ἆ", "Ἇ", "ᾈ", "ᾉ", "ᾊ", "ᾋ", "ᾌ", "ᾍ", "ᾎ", "ᾏ", "Ᾰ", "Ᾱ", "Ὰ", "Ά", "ᾼ", "А", "Ǻ", "Ǎ"} );
		chars.put( "B", new String[] {"Б", "Β", "ब"} );
		chars.put( "C", new String[] {"Ç", "Ć", "Č", "Ĉ", "Ċ"} );
		chars.put( "D", new String[] {"Ď", "Ð", "Đ", "Ɖ", "Ɗ", "Ƌ", "ᴅ", "ᴆ", "Д", "Δ"} );
		chars.put( "E", new String[] {"É", "È", "Ẻ", "Ẽ", "Ẹ", "Ê", "Ế", "Ề", "Ể", "Ễ", "Ệ", "Ë", "Ē", "Ę", "Ě", "Ĕ", "Ė", "Ε", "Έ", "Ἐ", "Ἑ", "Ἒ", "Ἓ", "Ἔ", "Ἕ", "Έ", "Ὲ", "Е", "Ё", "Э", "Є", "Ə"} );
		chars.put( "F", new String[] {"Ф", "Φ"} );
		chars.put( "G", new String[] {"Ğ", "Ġ", "Ģ", "Г", "Ґ", "Γ"} );
		chars.put( "H", new String[] {"Η", "Ή", "Ħ"} );
		chars.put( "I", new String[] {"Í", "Ì", "Ỉ", "Ĩ", "Ị", "Î", "Ï", "Ī", "Ĭ", "Į", "İ", "Ι", "Ί", "Ϊ", "Ἰ", "Ἱ", "Ἳ", "Ἴ", "Ἵ", "Ἶ", "Ἷ", "Ῐ", "Ῑ", "Ὶ", "Ί", "И", "І", "Ї", "Ǐ", "ϒ"} );
		chars.put( "K", new String[] {"К", "Κ"} );
		chars.put( "L", new String[] {"Ĺ", "Ł", "Л", "Λ", "Ļ", "Ľ", "Ŀ", "ल"} );
		chars.put( "M", new String[] {"М", "Μ"} );
		chars.put( "N", new String[] {"Ń", "Ñ", "Ň", "Ņ", "Ŋ", "Н", "Ν"} );
		chars.put( "O", new String[] {"Ó", "Ò", "Ỏ", "Õ", "Ọ", "Ô", "Ố", "Ồ", "Ổ", "Ỗ", "Ộ", "Ơ", "Ớ", "Ờ", "Ở", "Ỡ", "Ợ", "Ø", "Ō", "Ő", "Ŏ", "Ο", "Ό", "Ὀ", "Ὁ", "Ὂ", "Ὃ", "Ὄ", "Ὅ", "Ὸ", "Ό", "О", "Θ", "Ө", "Ǒ", "Ǿ"} );
		chars.put( "P", new String[] {"П", "Π"} );
		chars.put( "R", new String[] {"Ř", "Ŕ", "Р", "Ρ", "Ŗ"} );
		chars.put( "S", new String[] {"Ş", "Ŝ", "Ș", "Š", "Ś", "С", "Σ"} );
		chars.put( "T", new String[] {"Ť", "Ţ", "Ŧ", "Ț", "Т", "Τ"} );
		chars.put( "U", new String[] {"Ú", "Ù", "Ủ", "Ũ", "Ụ", "Ư", "Ứ", "Ừ", "Ử", "Ữ", "Ự", "Û", "Ū", "Ů", "Ű", "Ŭ", "Ų", "У", "Ǔ", "Ǖ", "Ǘ", "Ǚ", "Ǜ"} );
		chars.put( "V", new String[] {"В"} );
		chars.put( "W", new String[] {"Ω", "Ώ", "Ŵ"} );
		chars.put( "X", new String[] {"Χ", "Ξ"} );
		chars.put( "Y", new String[] {"Ý", "Ỳ", "Ỷ", "Ỹ", "Ỵ", "Ÿ", "Ῠ", "Ῡ", "Ὺ", "Ύ", "Ы", "Й", "Υ", "Ϋ", "Ŷ"} );
		chars.put( "Z", new String[] {"Ź", "Ž", "Ż", "З", "Ζ"} );
		chars.put( "AE", new String[] {"Ä", "Æ", "Ǽ"} );
		chars.put( "CH", new String[] {"Ч"} );
		chars.put( "DJ", new String[] {"Ђ"} );
		chars.put( "DZ", new String[] {"Џ"} );
		chars.put( "GX", new String[] {"Ĝ"} );
		chars.put( "HX", new String[] {"Ĥ"} );
		chars.put( "IJ", new String[] {"Ĳ"} );
		chars.put( "JX", new String[] {"Ĵ"} );
		chars.put( "KH", new String[] {"Х"} );
		chars.put( "LJ", new String[] {"Љ"} );
		chars.put( "NJ", new String[] {"Њ"} );
		chars.put( "OE", new String[] {"Ö", "Œ"} );
		chars.put( "PS", new String[] {"Ψ"} );
		chars.put( "SH", new String[] {"Ш"} );
		chars.put( "SHCH", new String[] {"Щ"} );
		chars.put( "SS", new String[] {"ẞ"} );
		chars.put( "TH", new String[] {"Þ"} );
		chars.put( "TS", new String[] {"Ц"} );
		chars.put( "UE", new String[] {"Ü"} );
		chars.put( "YA", new String[] {"Я"} );
		chars.put( "YU", new String[] {"Ю"} );
		chars.put( "ZH", new String[] {"Ж"} );
		chars.put( " ", new String[] {"\\xC2\\xA0", "\\xE2\\x80\\x80", "\\xE2\\x80\\x81", "\\xE2\\x80\\x82", "\\xE2\\x80\\x83", "\\xE2\\x80\\x84", "\\xE2\\x80\\x85", "\\xE2\\x80\\x86", "\\xE2\\x80\\x87", "\\xE2\\x80\\x88", "\\xE2\\x80\\x89", "\\xE2\\x80\\x8A", "\\xE2\\x80\\xAF", "\\xE2\\x81\\x9F", "\\xE3\\x80\\x80"} );

		CHARS_MAP = Collections.unmodifiableMap( chars );
	}

	public static String bytesToStringASCII( byte[] bytes )
	{
		return new String( bytes, StandardCharsets.US_ASCII );
	}

	public static String bytesToStringUTF( byte[] bytes )
	{
		return new String( bytes, StandardCharsets.UTF_8 );
	}

	/**
	 * Converts a camel case string to a namespace by detecting the presence of uppercase letters.
	 *
	 * @param camelCase the string to convert
	 *
	 * @return the resulting namespace, e.g.,
	 */
	@APINotice
	public static String camelToNamespace( @Nonnull String camelCase )
	{
		List<String> result = new ArrayList<>();

		Pattern pattern = Pattern.compile( "([A-Z]?[a-z]*)" );
		Matcher matcher = pattern.matcher( toAscii( camelCase ) );
		for ( ; matcher.find(); )
			result.add( matcher.group( 1 ) );

		return join( result, "." ).toLowerCase();
	}

	public static String capitalizeWords( String str )
	{
		return capitalizeWords( str, ' ' );
	}

	public static String capitalizeWords( String str, char delimiter )
	{
		if ( UtilityObjects.isEmpty( str ) )
			return str;

		UtilityObjects.notNull( delimiter );

		final char[] buffer = str.toCharArray();
		boolean capitalizeNext = true;
		for ( int i = 0; i < buffer.length; i++ )
		{
			final char ch = buffer[i];
			if ( ch == delimiter )
				capitalizeNext = true;
			else if ( capitalizeNext )
			{
				buffer[i] = Character.toTitleCase( ch );
				capitalizeNext = false;
			}
		}
		return new String( buffer );
	}

	public static String capitalizeWordsFully( String str )
	{
		return capitalizeWordsFully( str, ' ' );
	}

	public static String capitalizeWordsFully( String str, char delimiter )
	{
		if ( UtilityObjects.isEmpty( str ) )
			return str;

		return capitalizeWords( str.toLowerCase(), delimiter );
	}

	public static int codePointAt( char[] chars, int index )
	{
		if ( ( index < 0 ) || ( index >= chars.length ) )
			throw new StringIndexOutOfBoundsException( index );
		char c1 = chars[index];
		if ( c1 >= Character.MIN_HIGH_SURROGATE && c1 < ( Character.MAX_HIGH_SURROGATE + 1 ) && ++index < chars.length )
		{
			char c2 = chars[index];
			if ( c2 >= Character.MIN_LOW_SURROGATE && c2 < ( Character.MAX_LOW_SURROGATE + 1 ) )
				return ( ( c1 << 10 ) + c2 ) + ( Character.MIN_SUPPLEMENTARY_CODE_POINT - ( Character.MIN_HIGH_SURROGATE << 10 ) - Character.MIN_LOW_SURROGATE );
		}
		return c1;
	}

	/**
	 * Returns true if either array shares ANY elements
	 */
	public static boolean comparable( Object[] arrayLeft, Object[] arrayRight )
	{
		for ( Object objLeft : arrayLeft )
			for ( Object objRight : arrayRight )
				if ( objLeft != null && objLeft.equals( objRight ) )
					return true;
		return false;
	}

	/**
	 * Copies all elements from the iterable collection of originals to the collection provided.
	 *
	 * @param token      String to search for
	 * @param originals  An iterable collection of strings to filter.
	 * @param collection The collection to add matches to
	 *
	 * @return the collection provided that would have the elements copied into
	 *
	 * @throws UnsupportedOperationException if the collection is immutable and originals contains a string which starts with the specified search string.
	 * @throws IllegalArgumentException      if any parameter is is null
	 * @throws IllegalArgumentException      if originals contains a null element. <b>Note: the collection may be modified before this is thrown</b>
	 */
	public static <T extends Collection<String>> T copyPartialMatches( final String token, final Iterable<String> originals, final T collection ) throws UnsupportedOperationException, IllegalArgumentException
	{
		UtilityObjects.notNull( token, "Search token cannot be null" );
		UtilityObjects.notNull( collection, "Collection cannot be null" );
		UtilityObjects.notNull( originals, "Originals cannot be null" );

		for ( String string : originals )
			if ( startsWithIgnoreCase( string, token ) )
				collection.add( string );

		return collection;
	}

	public static int countMatches( String str, char chr )
	{
		int cnt = 0;
		for ( int i = 0; i < str.length(); i++ )
			if ( str.charAt( i ) == chr )
				cnt++;
		return cnt;
	}

	public static byte[] decodeDefault( @Nonnull String str )
	{
		return str.getBytes( Charset.defaultCharset() );
	}

	public static byte[] decodeUtf8( @Nonnull String str )
	{
		return str.getBytes( StandardCharsets.UTF_8 );
	}

	public static String encodeDefault( byte[] bytes )
	{
		return new String( bytes, Charset.defaultCharset() );
	}

	public static String encodeUtf8( byte[] bytes )
	{
		return new String( bytes, StandardCharsets.UTF_8 );
	}

	/**
	 * Provides a shorthand for changing if strings match while allowing for a case sensitive check.
	 *
	 * @param left          The first string
	 * @param right         The second string
	 * @param caseSensitive Is case sensitive
	 *
	 * @return Returns true if they match. If either string is null, we return false.
	 */
	public static boolean equals( @Nullable String left, @Nullable String right, boolean caseSensitive )
	{
		if ( left == null || right == null )
			return false;
		return caseSensitive ? left.equals( right ) : left.equalsIgnoreCase( right );
	}

	public static String escapeHtml( String str )
	{
		return EscapeTranslator.HTML_ESCAPE().translate( str );
	}

	@Deprecated
	public static String fixQuotes( String var )
	{
		try
		{
			var = var.replaceAll( "\\\\\"", "\"" );
			var = var.replaceAll( "\\\\'", "'" );

			if ( var.startsWith( "\"" ) || var.startsWith( "'" ) )
				var = var.substring( 1 );
			if ( var.endsWith( "\"" ) || var.endsWith( "'" ) )
				var = var.substring( 0, var.length() - 1 );
		}
		catch ( Exception ignore )
		{

		}

		return var;
	}

	@Nonnull
	public static String ifNullReturnEmpty( @Nullable String str )
	{
		return str == null ? "" : str;
	}

	public static boolean isCamelCase( @Nonnull String var )
	{
		return var.matches( "[a-z0-9]+(?:[A-Z]{1,2}[a-z0-9]+)*" );
	}

	public static boolean isCapitalizedWords( @Nonnull String str )
	{
		return str.equals( capitalizeWords( str ) );
	}

	public static boolean isCapitalizedWordsFully( @Nonnull String str )
	{
		return str.equals( capitalizeWordsFully( str ) );
	}

	public static boolean isEmpty( @Nullable String str )
	{
		return str != null && str.length() == 0;
	}

	/**
	 * Determines if a string is all lowercase using the toLowerCase() method.
	 *
	 * @param str The string to check
	 *
	 * @return Is it all lowercase?
	 */
	public static boolean isLowercase( String str )
	{
		return str.toLowerCase().equals( str );
	}

	public static boolean isNotEmpty( String str )
	{
		return str.length() > 0;
	}

	/**
	 * Determines if a string is all uppercase using the toUpperCase() method.
	 *
	 * @param str The string to check
	 *
	 * @return Is it all uppercase?
	 */
	public static boolean isUppercase( String str )
	{
		return str.toUpperCase().equals( str );
	}

	public static String join( @Nonnull Map<String, ?> args, @Nonnull String glue )
	{
		return join( args, glue, "=" );
	}

	public static String join( @Nonnull Map<String, ?> args )
	{
		return join( args, ", ", "=" );
	}

	public static String join( @Nonnull Map<String, ?> args, @Nonnull String glue, @Nonnull String keyValueSeparator )
	{
		return args.entrySet().stream().map( e -> e.getKey() + keyValueSeparator + UtilityObjects.castToString( e.getValue() ) ).collect( Collectors.joining( glue ) );
	}

	public static String join( @Nonnull Map<String, ?> args, @Nonnull String glue, @Nonnull String keyValueSeparator, @Nonnull String nullValue )
	{
		return args.entrySet().stream().map( e -> e.getKey() + keyValueSeparator + ( e.getValue() == null ? nullValue : UtilityObjects.castToString( e.getValue() ) ) ).collect( Collectors.joining( glue ) );
	}

	public static String join( @Nonnull Collection<String> args )
	{
		return join( args, ", " );
	}

	public static String join( @Nonnull Collection<String> args, @Nonnull String glue )
	{
		return args.stream().collect( Collectors.joining( glue ) );
	}

	public static <T> String join( @Nonnull Collection<T> args, @Nonnull Function<T, String> function )
	{
		return join( args, function, ", " );
	}

	public static <T> String join( @Nonnull Collection<T> args, @Nonnull Function<T, String> function, @Nonnull String glue )
	{
		return args.stream().map( function ).collect( Collectors.joining( glue ) );
	}

	public static String join( @Nonnull String[] args )
	{
		return join( args, ", " );
	}

	public static String join( @Nonnull String[] args, @Nonnull String glue )
	{
		return Arrays.stream( args ).collect( Collectors.joining( glue ) );
	}

	public static String join( @Nonnull int[] args )
	{
		return join( args, ", " );
	}

	public static String join( @Nonnull int[] args, @Nonnull String glue )
	{
		return Arrays.stream( args ).mapToObj( Integer::toString ).collect( Collectors.joining( glue ) );
	}

	public static String lcFirst( String value )
	{
		return value.substring( 0, 1 ).toLowerCase() + value.substring( 1 );
	}

	@Nonnegative
	public static int length( @Nullable String str )
	{
		return str == null ? 0 : str.length();
	}

	public static void lengthMustEqual( @Nonnull String str, @Nonnegative int len )
	{
		UtilityObjects.notFalse( str.length() == len, "String is lessThan or greatThan the required string length. {required=" + len + ",got=" + str.length() + "}" );
	}

	public static int lengthOrNeg( @Nullable String str )
	{
		return str == null ? -1 : str.length();
	}

	public static String limitLength( String str, int max )
	{
		return limitLength( str, max, true );
	}

	public static String limitLength( String str, int max, boolean appendEllipsis )
	{
		if ( str.length() <= max )
			return str;
		return str.substring( 0, max ) + ( appendEllipsis ? "..." : "" );
	}

	public static boolean matches( @Nullable String str, String... compareTo )
	{
		if ( str == null )
			return false;
		return Arrays.asList( compareTo ).contains( str );
	}

	public static boolean matchesIgnoreCase( @Nullable String str, String... compareTo )
	{
		if ( str == null )
			return false;
		return Arrays.asList( toLowerCase( compareTo ) ).contains( str.toLowerCase() );
	}

	@Nonnull
	public static String notEmptyOrDef( @Nullable String value, @Nonnull String def )
	{
		return isEmpty( value ) ? def : value;
	}

	public static Color parseColor( String color )
	{
		Pattern c = Pattern.compile( "rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)" );
		Matcher m = c.matcher( color );

		// First try to parse RGB(0,0,0);
		if ( m.matches() )
			return new Color( Integer.valueOf( m.group( 1 ) ), // r
					Integer.valueOf( m.group( 2 ) ), // g
					Integer.valueOf( m.group( 3 ) ) ); // b

		try
		{
			Field field = Class.forName( "java.awt.Color" ).getField( color.trim().toUpperCase() );
			return ( Color ) field.get( null );
		}
		catch ( Exception e )
		{
			// Ignore
		}

		try
		{
			return Color.decode( color );
		}
		catch ( Exception e )
		{
			// Ignore
		}

		return null;
	}

	public static Map<String, String> queryToMap( String query ) throws UnsupportedEncodingException
	{
		Map<String, String> result = new HashMap<>();

		if ( query == null )
			return result;

		for ( String param : query.split( "&" ) )
		{
			String[] pair = param.split( "=" );
			try
			{
				if ( pair.length > 1 )
					result.put( URLDecoder.decode( trimEnd( pair[0], '%' ), "ISO-8859-1" ), URLDecoder.decode( trimEnd( pair[1], '%' ), "ISO-8859-1" ) );
				else if ( pair.length == 1 )
					result.put( URLDecoder.decode( trimEnd( pair[0], '%' ), "ISO-8859-1" ), "" );
			}
			catch ( IllegalArgumentException e )
			{
				// Kernel.L.warning( "Malformed URL exception was thrown, key: `" + pair[0] + "`, val: '" + pair[1] + "'" );
			}
		}
		return result;
	}

	public static String randomChars( String seed, int length )
	{
		UtilityObjects.notEmpty( seed );

		StringBuilder sb = new StringBuilder();

		for ( int i = 0; i < length; i++ )
			sb.append( seed.toCharArray()[new Random().nextInt( seed.length() )] );

		return sb.toString();
	}

	@Nullable
	public static String regexCapture( @Nonnull String var, @Nonnull String regex )
	{
		return regexCapture( var, regex, 1 );
	}

	@Nullable
	public static String regexCapture( @Nonnull String var, @Nonnull String regex, int group )
	{
		Pattern p = Pattern.compile( regex );
		Matcher m = p.matcher( var );

		if ( !m.find() )
			return null;

		return m.group( group );
	}

	/**
	 * @deprecated Use Pattern.quote( str ) instead;
	 */
	@Deprecated
	public static String regexQuote( String str )
	{
		return Pattern.quote( str );
		/* if ( str == null )
			return null;
		for ( String s : new String[] {"\\", "+", ".", "?", "*", "[", "]", "^", "$", "(", ")", "{", "}", "=", "!", "<", ">", "|", ":", "-"} )
			str = str.replace( s, "\\" + s );
		return str; */
	}

	public static String removeInvalidChars( String ref )
	{
		return ref.replaceAll( "[^a-zA-Z0-9!#$%&'*+-/=?^_`{|}~@\\. ]", "" );
	}

	public static String removeLetters( String input )
	{
		return input.replaceAll( "[a-zA-Z]", "" );
	}

	public static String removeLettersLower( String input )
	{
		return input.replaceAll( "[a-z]", "" );
	}

	public static String removeLettersUpper( String input )
	{
		return input.replaceAll( "[A-Z]", "" );
	}

	public static String removeNumbers( String input )
	{
		return input.replaceAll( "\\d", "" );
	}

	public static String removeSpecial( String input )
	{
		return input.replaceAll( "\\W", "" );
	}

	public static String removeWhitespace( String input )
	{
		return input.replaceAll( "\\s", "" );
	}

	public static String repeat( @Nonnull String string, @Nonnegative int count )
	{
		if ( count <= 1 )
			return count == 0 ? "" : string;

		final int len = string.length();
		final long longSize = ( long ) len * ( long ) count;
		final int size = ( int ) longSize;
		if ( size != longSize )
			throw new ArrayIndexOutOfBoundsException( "Required array size too large: " + longSize );

		final char[] array = new char[size];
		string.getChars( 0, len, array, 0 );
		int n;
		for ( n = len; n < size - n; n <<= 1 )
			System.arraycopy( array, 0, array, n, n );
		System.arraycopy( array, 0, array, n, size - n );
		return new String( array );
	}

	public static List<String> repeatToList( @Nonnull String str, @Nonnegative int length )
	{
		List<String> list = new ArrayList<>();
		for ( int i = 0; i < length; i++ )
			list.add( str );
		return list;
	}

	public static String replaceAt( @Nonnull String str, @Nonnegative int at, char replacement )
	{
		StringBuilder sb = new StringBuilder( str );
		sb.setCharAt( at, replacement );
		return sb.toString();
	}

	public static String slugify( @Nonnull String str )
	{
		return slugify( str, "-" );
	}

	public static String slugify( @Nonnull String str, @Nonnull String glue )
	{
		str = toAscii( str );

		// Convert all dashes/underscores into separator
		String flip = "-".equals( glue ) ? "_" : "-";
		str = str.replaceAll( "![" + regexQuote( flip ) + "]+!u", glue );

		// Remove all characters that are not the separator, letters, numbers, or whitespace.
		str = str.toLowerCase().replaceAll( "![^" + regexQuote( glue ) + "\\pL\\pN\\s]+!u", "" );

		// Replace all separator characters and whitespace by a single separator
		str = str.replaceAll( "![" + regexQuote( glue ) + "\\s]+!u", glue );

		return trimAll( str, glue.charAt( 0 ) );
	}

	public static Stream<String> split( @Nonnull String str, @Nonnull String delimiter, @Nonnegative int limit )
	{
		if ( UtilityObjects.isEmpty( str ) )
			return Stream.empty();
		return Arrays.stream( str.split( delimiter, limit ) );
	}

	public static Stream<String> split( @Nonnull String str, @Nonnull String delimiter )
	{
		if ( UtilityObjects.isEmpty( str ) )
			return Stream.empty();
		return Arrays.stream( str.split( delimiter ) );
	}

	public static Stream<String> split( @Nonnull String str, @Nonnull Pattern delimiter )
	{
		if ( UtilityObjects.isEmpty( str ) )
			return Stream.empty();
		return Arrays.stream( delimiter.split( str ) );
	}

	public static Stream<String> splitLiteral( @Nonnull String str, @Nonnull String delimiter )
	{
		return split( str, Pattern.compile( delimiter, Pattern.LITERAL ) );
	}

	/**
	 * This method uses a substring to check case-insensitive equality. This means the internal array does not need to be
	 * copied like a toLowerCase() call would.
	 *
	 * @param str      String to check
	 * @param prefixes Prefix of string to compare
	 *
	 * @return true if provided string starts with, ignoring case, the prefix provided
	 *
	 * @throws NullPointerException if prefix is null
	 */
	public static boolean startsWithIgnoreCase( @Nonnull final String str, @Nonnull final String... prefixes ) throws NullPointerException
	{
		for ( String prefix : prefixes )
			if ( str.length() >= prefix.length() && str.substring( 0, prefix.length() ).equalsIgnoreCase( prefix ) )
				return true;
		return false;
	}

	public static StringChain stringChain( @Nonnull String str )
	{
		return new StringChain( str );
	}

	public static byte[] stringToBytesASCII( String str )
	{
		return str == null ? null : str.getBytes( StandardCharsets.US_ASCII );

		/*byte[] b = new byte[str.length()];
		for ( int i = 0; i < b.length; i++ )
			b[i] = ( byte ) str.charAt( i );
		return b;*/
	}

	public static byte[] stringToBytesUTF( String str )
	{
		return str == null ? null : str.getBytes( StandardCharsets.UTF_8 );

		/*byte[] b = new byte[str.length() << 1];
		for ( int i = 0; i < str.length(); i++ )
		{
			char strChar = str.charAt( i );
			int bytePos = i << 1;
			b[bytePos] = ( byte ) ( ( strChar & 0xFF00 ) >> 8 );
			b[bytePos + 1] = ( byte ) ( strChar & 0x00FF );
		}
		return b;*/
	}

	public static String toAscii( String str )
	{
		for ( Map.Entry<String, String[]> charSet : CHARS_MAP.entrySet() )
			str = str.replaceAll( join( charSet.getValue(), "|" ), charSet.getKey() );
		return str.replaceAll( "[^\\x20-\\x7E]", "" );
	}

	/**
	 * Convert a value to camel case.
	 *
	 * @param value
	 *
	 * @return String
	 */
	public static String toCamelCase( String value )
	{
		return lcFirst( toStudlyCase( value ) );
	}

	/**
	 * Scans a string list for entries that are not lower case.
	 *
	 * @param strings The original list to check.
	 *
	 * @return Lowercase string array.
	 */
	public static List<String> toLowerCase( List<String> strings )
	{
		return strings.stream().filter( v -> !UtilityObjects.isNull( v ) ).map( String::toLowerCase ).collect( Collectors.toList() );
	}

	public static Set<String> toLowerCase( Set<String> strings )
	{
		return strings.stream().filter( v -> !UtilityObjects.isNull( v ) ).map( String::toLowerCase ).collect( Collectors.toSet() );
	}

	public static String[] toLowerCase( String[] strings )
	{
		return Arrays.stream( strings ).filter( v -> !UtilityObjects.isNull( v ) ).map( String::toLowerCase ).toArray( String[]::new );
	}

	public static char[] toLowerCase( char[] chars )
	{
		return toLowerCase( chars, Locale.getDefault() );
	}

	public static char[] toLowerCase( char[] chars, Locale locale )
	{
		int firstUpper;
		final int len = chars.length;

		/* Now check if there are any characters that need to be changed. */
		scan:
		{
			for ( firstUpper = 0; firstUpper < len; )
			{
				char c = chars[firstUpper];
				if ( ( c >= Character.MIN_HIGH_SURROGATE ) && ( c <= Character.MAX_HIGH_SURROGATE ) )
				{
					int supplChar = codePointAt( chars, firstUpper );
					if ( supplChar != Character.toLowerCase( supplChar ) )
					{
						break scan;
					}
					firstUpper += Character.charCount( supplChar );
				}
				else
				{
					if ( c != Character.toLowerCase( c ) )
					{
						break scan;
					}
					firstUpper++;
				}
			}
			return Arrays.copyOf( chars, chars.length );
		}

		char[] result = new char[len];
		int resultOffset = 0;  /* result may grow, so i+resultOffset
		 * is the write location in result */

		/* Just copy the first few lowerCase characters. */
		System.arraycopy( chars, 0, result, 0, firstUpper );

		String lang = locale.getLanguage();
		boolean localeDependent = ( lang == "tr" || lang == "az" || lang == "lt" );
		char[] lowerCharArray;
		int lowerChar;
		int srcChar;
		int srcCount;
		for ( int i = firstUpper; i < len; i += srcCount )
		{
			srcChar = ( int ) chars[i];
			if ( ( char ) srcChar >= Character.MIN_HIGH_SURROGATE && ( char ) srcChar <= Character.MAX_HIGH_SURROGATE )
			{
				srcChar = codePointAt( chars, i );
				srcCount = Character.charCount( srcChar );
			}
			else
			{
				srcCount = 1;
			}
			if ( localeDependent || srcChar == '\u03A3' || // GREEK CAPITAL LETTER SIGMA
					srcChar == '\u0130' )
			{ // LATIN CAPITAL LETTER I WITH DOT ABOVE
				lowerChar = ConditionalSpecialCasing.toLowerCaseEx( new String( chars ), i, locale );
			}
			else
			{
				lowerChar = Character.toLowerCase( srcChar );
			}
			if ( ( lowerChar == 0xFFFFFFFF ) || ( lowerChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT ) )
			{
				// Character.ERROR = 0xFFFFFFFF
				if ( lowerChar == 0xFFFFFFFF )
				{
					lowerCharArray = ConditionalSpecialCasing.toLowerCaseCharArray( new String( chars ), i, locale );
				}
				else if ( srcCount == 2 )
				{
					resultOffset += Character.toChars( lowerChar, result, i + resultOffset ) - srcCount;
					continue;
				}
				else
				{
					lowerCharArray = Character.toChars( lowerChar );
				}

				/* Grow result if needed */
				int mapLen = lowerCharArray.length;
				if ( mapLen > srcCount )
				{
					char[] result2 = new char[result.length + mapLen - srcCount];
					System.arraycopy( result, 0, result2, 0, i + resultOffset );
					result = result2;
				}
				for ( int x = 0; x < mapLen; ++x )
				{
					result[i + resultOffset + x] = lowerCharArray[x];
				}
				resultOffset += ( mapLen - srcCount );
			}
			else
			{
				result[i + resultOffset] = ( char ) lowerChar;
			}
		}
		return Arrays.copyOf( result, len + resultOffset );
		// return new String( result, 0, len + resultOffset );
	}

	/**
	 * Convert a value to studly caps case.
	 *
	 * @param value
	 *
	 * @return String
	 */
	public static String toStudlyCase( String value )
	{
		return capitalizeWordsFully( value.replaceAll( "[.-_/\\\\]", " " ).trim() ).replaceAll( " ", "" );
	}

	public static String toUnicode( String str )
	{
		// TODO Implement
		return IDN.toUnicode( str );
	}

	public static String trimAll( @Nullable String text )
	{
		return trimAllRegex( text, "\\s|\\n|\\t|\\r" );
	}

	/**
	 * Trim specified character from both ends of a String
	 *
	 * @param text       Text
	 * @param characters Characters to remove
	 *
	 * @return Trimmed text
	 */
	public static String trimAll( @Nullable String text, char... characters )
	{
		String normalizedText = trimStart( text, characters );
		return trimEnd( normalizedText, characters );
	}

	public static String trimAllRegex( @Nullable String text, String regex )
	{
		text = trimStartRegex( text, regex );
		return trimEndRegex( text, regex );
	}

	/**
	 * Trim specified character from end of string
	 *
	 * @param text       Text
	 * @param characters Character to remove
	 *
	 * @return Trimmed text
	 */
	public static String trimEnd( @Nullable String text, char... characters )
	{
		String normalizedText;
		int index;

		if ( text == null || text.isEmpty() )
			return text;

		normalizedText = text.trim();
		index = normalizedText.length() - 1;

		while ( UtilityArrs.contains( characters, normalizedText.charAt( index ) ) )
			if ( --index < 0 )
				return "";
		return normalizedText.substring( 0, index + 1 ).trim();
	}

	public static String trimEnd( @Nullable String text, String substr )
	{
		if ( UtilityObjects.isEmpty( text ) )
			return text;

		if ( text.trim().endsWith( substr ) )
			text = text.trim().substring( 0, text.trim().length() - substr.length() ).trim();

		return text;
	}

	public static String trimEndRegex( @Nullable String text, String regex )
	{
		if ( length( text ) < 1 )
			return "";

		if ( !SIMPLE_REGEX_PATTERN_CHARS.matcher( text ).matches() )
			throw new IllegalArgumentException( "Regex contains disallowed characters." );

		Pattern sweeper = Pattern.compile( "^(.*)(" + regex + ")$" );
		Matcher matcher = sweeper.matcher( text );

		while ( matcher.find() )
		{
			text = matcher.replaceFirst( "$1" );
			matcher = sweeper.matcher( text );
		}

		return text;
	}

	/**
	 * Trim specified character from front of string
	 *
	 * @param text       Text
	 * @param characters Character to remove
	 *
	 * @return Trimmed text
	 */
	public static String trimStart( @Nullable String text, char... characters )
	{
		if ( UtilityObjects.isEmpty( text ) )
			return text;

		String normalizedText = text.trim();
		int index = -1;

		do
			index++;
		while ( index < normalizedText.length() && UtilityArrs.contains( characters, normalizedText.charAt( index ) ) );

		return normalizedText.substring( index ).trim();
	}

	public static String trimStart( @Nullable String text, String substr )
	{
		if ( UtilityObjects.isEmpty( text ) )
			return text;

		if ( text.trim().startsWith( substr ) )
			text = text.trim().substring( substr.length() ).trim();

		return text;
	}

	public static String trimStartRegex( @Nullable String text, String regex )
	{
		if ( length( text ) < 1 )
			return "";

		if ( !SIMPLE_REGEX_PATTERN_CHARS.matcher( text ).matches() )
			throw new IllegalArgumentException( "Regex contains disallowed characters." );

		Pattern sweeper = Pattern.compile( "^(" + regex + ")(.*)$" );
		Matcher matcher = sweeper.matcher( text );

		while ( matcher.find() )
		{
			text = matcher.replaceFirst( "$2" );
			matcher = sweeper.matcher( text );
		}

		return text;
	}

	public static <T extends Collection<String>> T wrap( T list )
	{
		return wrap( list, '`' );
	}

	public static <T extends Collection<String>> T wrap( T list, char wrapChar )
	{
		synchronized ( list )
		{
			String[] strings = list.toArray( new String[0] );
			list.clear();
			for ( int i = 0; i < strings.length; i++ )
				list.add( wrap( strings[i], wrapChar ) );
		}

		return list;
	}

	public static <T extends Map<String, String>> T wrap( T map )
	{
		return wrap( map, '`', '\'' );
	}

	public static <T extends Map<String, String>> T wrap( T map, char keyChar, char valueChar )
	{
		Map<String, String> tmpMap = new HashMap<>( map );
		map.clear();

		for ( Map.Entry<String, String> entry : tmpMap.entrySet() )
			if ( !isEmpty( entry.getKey() ) )
				map.put( keyChar + entry.getKey() + keyChar, valueChar + ( entry.getValue() == null ? "" : entry.getValue() ) + valueChar );

		return map;
	}

	public static String wrap( String str, char wrap )
	{
		return String.format( "%s%s%s", wrap, str, wrap );
	}

	private UtilityStrings()
	{

	}

	/**
	 * Mirrored from {@see java.lang.ConditionalSpecialCasing} for public access
	 */
	public static class ConditionalSpecialCasing
	{
		// TODO Implement failover in the event that the JDK does not implement the class or methods.

		private static <R> R invokeMethod( String methodName, Object... args )
		{
			return UtilityObjects.invokeStaticMethod( "java.lang.ConditionalSpecialCasing", methodName, args );
		}

		public static char[] toLowerCaseCharArray( String src, int index, Locale locale )
		{
			return invokeMethod( "toLowerCaseCharArray", src, index, locale );
		}

		public static int toLowerCaseEx( String src, int index, Locale locale )
		{
			return invokeMethod( "toLowerCaseEx", src, index, locale );
		}

		public static char[] toUpperCaseCharArray( String src, int index, Locale locale )
		{
			return invokeMethod( "toUpperCaseCharArray", src, index, locale );
		}

		public static int toUpperCaseEx( String src, int index, Locale locale )
		{
			return invokeMethod( "toUpperCaseEx", src, index, locale );
		}
	}

	public static class EscapeTranslator
	{
		private static final Map<String, String> BASIC_ESCAPE;
		private static final Map<String, String> HTML_ESCAPE;
		private static final Map<String, String> ISO8859_1_ESCAPE;
		private static final Map<String, String> JAVA_CTRL_CHARS_ESCAPE;

		static
		{
			UtilityMaps.MapBuilder<String, String> iso88591Builder = UtilityMaps.builder();
			iso88591Builder.put( "\u00A0", "&nbsp;" ); // non-breaking space
			iso88591Builder.put( "\u00A1", "&iexcl;" ); // inverted exclamation mark
			iso88591Builder.put( "\u00A2", "&cent;" ); // cent sign
			iso88591Builder.put( "\u00A3", "&pound;" ); // pound sign
			iso88591Builder.put( "\u00A4", "&curren;" ); // currency sign
			iso88591Builder.put( "\u00A5", "&yen;" ); // yen sign = yuan sign
			iso88591Builder.put( "\u00A6", "&brvbar;" ); // broken bar = broken vertical bar
			iso88591Builder.put( "\u00A7", "&sect;" ); // section sign
			iso88591Builder.put( "\u00A8", "&uml;" ); // diaeresis = spacing diaeresis
			iso88591Builder.put( "\u00A9", "&copy;" ); // � - copyright sign
			iso88591Builder.put( "\u00AA", "&ordf;" ); // feminine ordinal indicator
			iso88591Builder.put( "\u00AB", "&laquo;" ); // left-pointing double angle quotation mark = left pointing guillemet
			iso88591Builder.put( "\u00AC", "&not;" ); // not sign
			iso88591Builder.put( "\u00AD", "&shy;" ); // soft hyphen = discretionary hyphen
			iso88591Builder.put( "\u00AE", "&reg;" ); // � - registered trademark sign
			iso88591Builder.put( "\u00AF", "&macr;" ); // macron = spacing macron = overline = APL overbar
			iso88591Builder.put( "\u00B0", "&deg;" ); // degree sign
			iso88591Builder.put( "\u00B1", "&plusmn;" ); // plus-minus sign = plus-or-minus sign
			iso88591Builder.put( "\u00B2", "&sup2;" ); // superscript two = superscript digit two = squared
			iso88591Builder.put( "\u00B3", "&sup3;" ); // superscript three = superscript digit three = cubed
			iso88591Builder.put( "\u00B4", "&acute;" ); // acute accent = spacing acute
			iso88591Builder.put( "\u00B5", "&micro;" ); // micro sign
			iso88591Builder.put( "\u00B6", "&para;" ); // pilcrow sign = paragraph sign
			iso88591Builder.put( "\u00B7", "&middot;" ); // middle dot = Georgian comma = Greek middle dot
			iso88591Builder.put( "\u00B8", "&cedil;" ); // cedilla = spacing cedilla
			iso88591Builder.put( "\u00B9", "&sup1;" ); // superscript one = superscript digit one
			iso88591Builder.put( "\u00BA", "&ordm;" ); // masculine ordinal indicator
			iso88591Builder.put( "\u00BB", "&raquo;" ); // right-pointing double angle quotation mark = right pointing guillemet
			iso88591Builder.put( "\u00BC", "&frac14;" ); // vulgar fraction one quarter = fraction one quarter
			iso88591Builder.put( "\u00BD", "&frac12;" ); // vulgar fraction one half = fraction one half
			iso88591Builder.put( "\u00BE", "&frac34;" ); // vulgar fraction three quarters = fraction three quarters
			iso88591Builder.put( "\u00BF", "&iquest;" ); // inverted question mark = turned question mark
			iso88591Builder.put( "\u00C0", "&Agrave;" ); // � - uppercase A, grave accent
			iso88591Builder.put( "\u00C1", "&Aacute;" ); // � - uppercase A, acute accent
			iso88591Builder.put( "\u00C2", "&Acirc;" ); // � - uppercase A, circumflex accent
			iso88591Builder.put( "\u00C3", "&Atilde;" ); // � - uppercase A, tilde
			iso88591Builder.put( "\u00C4", "&Auml;" ); // � - uppercase A, umlaut
			iso88591Builder.put( "\u00C5", "&Aring;" ); // � - uppercase A, ring
			iso88591Builder.put( "\u00C6", "&AElig;" ); // � - uppercase AE
			iso88591Builder.put( "\u00C7", "&Ccedil;" ); // � - uppercase C, cedilla
			iso88591Builder.put( "\u00C8", "&Egrave;" ); // � - uppercase E, grave accent
			iso88591Builder.put( "\u00C9", "&Eacute;" ); // � - uppercase E, acute accent
			iso88591Builder.put( "\u00CA", "&Ecirc;" ); // � - uppercase E, circumflex accent
			iso88591Builder.put( "\u00CB", "&Euml;" ); // � - uppercase E, umlaut
			iso88591Builder.put( "\u00CC", "&Igrave;" ); // � - uppercase I, grave accent
			iso88591Builder.put( "\u00CD", "&Iacute;" ); // � - uppercase I, acute accent
			iso88591Builder.put( "\u00CE", "&Icirc;" ); // � - uppercase I, circumflex accent
			iso88591Builder.put( "\u00CF", "&Iuml;" ); // � - uppercase I, umlaut
			iso88591Builder.put( "\u00D0", "&ETH;" ); // � - uppercase Eth, Icelandic
			iso88591Builder.put( "\u00D1", "&Ntilde;" ); // � - uppercase N, tilde
			iso88591Builder.put( "\u00D2", "&Ograve;" ); // � - uppercase O, grave accent
			iso88591Builder.put( "\u00D3", "&Oacute;" ); // � - uppercase O, acute accent
			iso88591Builder.put( "\u00D4", "&Ocirc;" ); // � - uppercase O, circumflex accent
			iso88591Builder.put( "\u00D5", "&Otilde;" ); // � - uppercase O, tilde
			iso88591Builder.put( "\u00D6", "&Ouml;" ); // � - uppercase O, umlaut
			iso88591Builder.put( "\u00D7", "&times;" ); // multiplication sign
			iso88591Builder.put( "\u00D8", "&Oslash;" ); // � - uppercase O, slash
			iso88591Builder.put( "\u00D9", "&Ugrave;" ); // � - uppercase U, grave accent
			iso88591Builder.put( "\u00DA", "&Uacute;" ); // � - uppercase U, acute accent
			iso88591Builder.put( "\u00DB", "&Ucirc;" ); // � - uppercase U, circumflex accent
			iso88591Builder.put( "\u00DC", "&Uuml;" ); // � - uppercase U, umlaut
			iso88591Builder.put( "\u00DD", "&Yacute;" ); // � - uppercase Y, acute accent
			iso88591Builder.put( "\u00DE", "&THORN;" ); // � - uppercase THORN, Icelandic
			iso88591Builder.put( "\u00DF", "&szlig;" ); // � - lowercase sharps, German
			iso88591Builder.put( "\u00E0", "&agrave;" ); // � - lowercase a, grave accent
			iso88591Builder.put( "\u00E1", "&aacute;" ); // � - lowercase a, acute accent
			iso88591Builder.put( "\u00E2", "&acirc;" ); // � - lowercase a, circumflex accent
			iso88591Builder.put( "\u00E3", "&atilde;" ); // � - lowercase a, tilde
			iso88591Builder.put( "\u00E4", "&auml;" ); // � - lowercase a, umlaut
			iso88591Builder.put( "\u00E5", "&aring;" ); // � - lowercase a, ring
			iso88591Builder.put( "\u00E6", "&aelig;" ); // � - lowercase ae
			iso88591Builder.put( "\u00E7", "&ccedil;" ); // � - lowercase c, cedilla
			iso88591Builder.put( "\u00E8", "&egrave;" ); // � - lowercase e, grave accent
			iso88591Builder.put( "\u00E9", "&eacute;" ); // � - lowercase e, acute accent
			iso88591Builder.put( "\u00EA", "&ecirc;" ); // � - lowercase e, circumflex accent
			iso88591Builder.put( "\u00EB", "&euml;" ); // � - lowercase e, umlaut
			iso88591Builder.put( "\u00EC", "&igrave;" ); // � - lowercase i, grave accent
			iso88591Builder.put( "\u00ED", "&iacute;" ); // � - lowercase i, acute accent
			iso88591Builder.put( "\u00EE", "&icirc;" ); // � - lowercase i, circumflex accent
			iso88591Builder.put( "\u00EF", "&iuml;" ); // � - lowercase i, umlaut
			iso88591Builder.put( "\u00F0", "&eth;" ); // � - lowercase eth, Icelandic
			iso88591Builder.put( "\u00F1", "&ntilde;" ); // � - lowercase n, tilde
			iso88591Builder.put( "\u00F2", "&ograve;" ); // � - lowercase o, grave accent
			iso88591Builder.put( "\u00F3", "&oacute;" ); // � - lowercase o, acute accent
			iso88591Builder.put( "\u00F4", "&ocirc;" ); // � - lowercase o, circumflex accent
			iso88591Builder.put( "\u00F5", "&otilde;" ); // � - lowercase o, tilde
			iso88591Builder.put( "\u00F6", "&ouml;" ); // � - lowercase o, umlaut
			iso88591Builder.put( "\u00F7", "&divide;" ); // division sign
			iso88591Builder.put( "\u00F8", "&oslash;" ); // � - lowercase o, slash
			iso88591Builder.put( "\u00F9", "&ugrave;" ); // � - lowercase u, grave accent
			iso88591Builder.put( "\u00FA", "&uacute;" ); // � - lowercase u, acute accent
			iso88591Builder.put( "\u00FB", "&ucirc;" ); // � - lowercase u, circumflex accent
			iso88591Builder.put( "\u00FC", "&uuml;" ); // � - lowercase u, umlaut
			iso88591Builder.put( "\u00FD", "&yacute;" ); // � - lowercase y, acute accent
			iso88591Builder.put( "\u00FE", "&thorn;" ); // � - lowercase thorn, Icelandic
			iso88591Builder.put( "\u00FF", "&yuml;" ); // � - lowercase y, umlaut
			ISO8859_1_ESCAPE = iso88591Builder.hashMap();

			UtilityMaps.MapBuilder<String, String> htmlBuilder = UtilityMaps.builder();
			// <!-- Latin Extended-B -->
			htmlBuilder.put( "\u0192", "&fnof;" ); // latin small f with hook = function= florin, U+0192 ISOtech -->
			// <!-- Greek -->
			htmlBuilder.put( "\u0391", "&Alpha;" ); // greek capital letter alpha, U+0391 -->
			htmlBuilder.put( "\u0392", "&Beta;" ); // greek capital letter beta, U+0392 -->
			htmlBuilder.put( "\u0393", "&Gamma;" ); // greek capital letter gamma,U+0393 ISOgrk3 -->
			htmlBuilder.put( "\u0394", "&Delta;" ); // greek capital letter delta,U+0394 ISOgrk3 -->
			htmlBuilder.put( "\u0395", "&Epsilon;" ); // greek capital letter epsilon, U+0395 -->
			htmlBuilder.put( "\u0396", "&Zeta;" ); // greek capital letter zeta, U+0396 -->
			htmlBuilder.put( "\u0397", "&Eta;" ); // greek capital letter eta, U+0397 -->
			htmlBuilder.put( "\u0398", "&Theta;" ); // greek capital letter theta,U+0398 ISOgrk3 -->
			htmlBuilder.put( "\u0399", "&Iota;" ); // greek capital letter iota, U+0399 -->
			htmlBuilder.put( "\u039A", "&Kappa;" ); // greek capital letter kappa, U+039A -->
			htmlBuilder.put( "\u039B", "&Lambda;" ); // greek capital letter lambda,U+039B ISOgrk3 -->
			htmlBuilder.put( "\u039C", "&Mu;" ); // greek capital letter mu, U+039C -->
			htmlBuilder.put( "\u039D", "&Nu;" ); // greek capital letter nu, U+039D -->
			htmlBuilder.put( "\u039E", "&Xi;" ); // greek capital letter xi, U+039E ISOgrk3 -->
			htmlBuilder.put( "\u039F", "&Omicron;" ); // greek capital letter omicron, U+039F -->
			htmlBuilder.put( "\u03A0", "&Pi;" ); // greek capital letter pi, U+03A0 ISOgrk3 -->
			htmlBuilder.put( "\u03A1", "&Rho;" ); // greek capital letter rho, U+03A1 -->
			// <!-- there is no Sigmaf, and no U+03A2 character either -->
			htmlBuilder.put( "\u03A3", "&Sigma;" ); // greek capital letter sigma,U+03A3 ISOgrk3 -->
			htmlBuilder.put( "\u03A4", "&Tau;" ); // greek capital letter tau, U+03A4 -->
			htmlBuilder.put( "\u03A5", "&Upsilon;" ); // greek capital letter upsilon,U+03A5 ISOgrk3 -->
			htmlBuilder.put( "\u03A6", "&Phi;" ); // greek capital letter phi,U+03A6 ISOgrk3 -->
			htmlBuilder.put( "\u03A7", "&Chi;" ); // greek capital letter chi, U+03A7 -->
			htmlBuilder.put( "\u03A8", "&Psi;" ); // greek capital letter psi,U+03A8 ISOgrk3 -->
			htmlBuilder.put( "\u03A9", "&Omega;" ); // greek capital letter omega,U+03A9 ISOgrk3 -->
			htmlBuilder.put( "\u03B1", "&alpha;" ); // greek small letter alpha,U+03B1 ISOgrk3 -->
			htmlBuilder.put( "\u03B2", "&beta;" ); // greek small letter beta, U+03B2 ISOgrk3 -->
			htmlBuilder.put( "\u03B3", "&gamma;" ); // greek small letter gamma,U+03B3 ISOgrk3 -->
			htmlBuilder.put( "\u03B4", "&delta;" ); // greek small letter delta,U+03B4 ISOgrk3 -->
			htmlBuilder.put( "\u03B5", "&epsilon;" ); // greek small letter epsilon,U+03B5 ISOgrk3 -->
			htmlBuilder.put( "\u03B6", "&zeta;" ); // greek small letter zeta, U+03B6 ISOgrk3 -->
			htmlBuilder.put( "\u03B7", "&eta;" ); // greek small letter eta, U+03B7 ISOgrk3 -->
			htmlBuilder.put( "\u03B8", "&theta;" ); // greek small letter theta,U+03B8 ISOgrk3 -->
			htmlBuilder.put( "\u03B9", "&iota;" ); // greek small letter iota, U+03B9 ISOgrk3 -->
			htmlBuilder.put( "\u03BA", "&kappa;" ); // greek small letter kappa,U+03BA ISOgrk3 -->
			htmlBuilder.put( "\u03BB", "&lambda;" ); // greek small letter lambda,U+03BB ISOgrk3 -->
			htmlBuilder.put( "\u03BC", "&mu;" ); // greek small letter mu, U+03BC ISOgrk3 -->
			htmlBuilder.put( "\u03BD", "&nu;" ); // greek small letter nu, U+03BD ISOgrk3 -->
			htmlBuilder.put( "\u03BE", "&xi;" ); // greek small letter xi, U+03BE ISOgrk3 -->
			htmlBuilder.put( "\u03BF", "&omicron;" ); // greek small letter omicron, U+03BF NEW -->
			htmlBuilder.put( "\u03C0", "&pi;" ); // greek small letter pi, U+03C0 ISOgrk3 -->
			htmlBuilder.put( "\u03C1", "&rho;" ); // greek small letter rho, U+03C1 ISOgrk3 -->
			htmlBuilder.put( "\u03C2", "&sigmaf;" ); // greek small letter final sigma,U+03C2 ISOgrk3 -->
			htmlBuilder.put( "\u03C3", "&sigma;" ); // greek small letter sigma,U+03C3 ISOgrk3 -->
			htmlBuilder.put( "\u03C4", "&tau;" ); // greek small letter tau, U+03C4 ISOgrk3 -->
			htmlBuilder.put( "\u03C5", "&upsilon;" ); // greek small letter upsilon,U+03C5 ISOgrk3 -->
			htmlBuilder.put( "\u03C6", "&phi;" ); // greek small letter phi, U+03C6 ISOgrk3 -->
			htmlBuilder.put( "\u03C7", "&chi;" ); // greek small letter chi, U+03C7 ISOgrk3 -->
			htmlBuilder.put( "\u03C8", "&psi;" ); // greek small letter psi, U+03C8 ISOgrk3 -->
			htmlBuilder.put( "\u03C9", "&omega;" ); // greek small letter omega,U+03C9 ISOgrk3 -->
			htmlBuilder.put( "\u03D1", "&thetasym;" ); // greek small letter theta symbol,U+03D1 NEW -->
			htmlBuilder.put( "\u03D2", "&upsih;" ); // greek upsilon with hook symbol,U+03D2 NEW -->
			htmlBuilder.put( "\u03D6", "&piv;" ); // greek pi symbol, U+03D6 ISOgrk3 -->
			// <!-- General Punctuation -->
			htmlBuilder.put( "\u2022", "&bull;" ); // bullet = black small circle,U+2022 ISOpub -->
			// <!-- bullet is NOT the same as bullet operator, U+2219 -->
			htmlBuilder.put( "\u2026", "&hellip;" ); // horizontal ellipsis = three dot leader,U+2026 ISOpub -->
			htmlBuilder.put( "\u2032", "&prime;" ); // prime = minutes = feet, U+2032 ISOtech -->
			htmlBuilder.put( "\u2033", "&Prime;" ); // double prime = seconds = inches,U+2033 ISOtech -->
			htmlBuilder.put( "\u203E", "&oline;" ); // overline = spacing overscore,U+203E NEW -->
			htmlBuilder.put( "\u2044", "&frasl;" ); // fraction slash, U+2044 NEW -->
			// <!-- Letterlike Symbols -->
			htmlBuilder.put( "\u2118", "&weierp;" ); // script capital P = power set= Weierstrass p, U+2118 ISOamso -->
			htmlBuilder.put( "\u2111", "&image;" ); // blackletter capital I = imaginary part,U+2111 ISOamso -->
			htmlBuilder.put( "\u211C", "&real;" ); // blackletter capital R = real part symbol,U+211C ISOamso -->
			htmlBuilder.put( "\u2122", "&trade;" ); // trade mark sign, U+2122 ISOnum -->
			htmlBuilder.put( "\u2135", "&alefsym;" ); // alef symbol = first transfinite cardinal,U+2135 NEW -->
			// <!-- alef symbol is NOT the same as hebrew letter alef,U+05D0 although the
			// same glyph could be used to depict both characters -->
			// <!-- Arrows -->
			htmlBuilder.put( "\u2190", "&larr;" ); // leftwards arrow, U+2190 ISOnum -->
			htmlBuilder.put( "\u2191", "&uarr;" ); // upwards arrow, U+2191 ISOnum-->
			htmlBuilder.put( "\u2192", "&rarr;" ); // rightwards arrow, U+2192 ISOnum -->
			htmlBuilder.put( "\u2193", "&darr;" ); // downwards arrow, U+2193 ISOnum -->
			htmlBuilder.put( "\u2194", "&harr;" ); // left right arrow, U+2194 ISOamsa -->
			htmlBuilder.put( "\u21B5", "&crarr;" ); // downwards arrow with corner leftwards= carriage return, U+21B5 NEW -->
			htmlBuilder.put( "\u21D0", "&lArr;" ); // leftwards double arrow, U+21D0 ISOtech -->
			// <!-- ISO 10646 does not say that lArr is the same as the 'is implied by'
			// arrow but also does not have any other character for that function.
			// So ? lArr canbe used for 'is implied by' as ISOtech suggests -->
			htmlBuilder.put( "\u21D1", "&uArr;" ); // upwards double arrow, U+21D1 ISOamsa -->
			htmlBuilder.put( "\u21D2", "&rArr;" ); // rightwards double arrow,U+21D2 ISOtech -->
			// <!-- ISO 10646 does not say this is the 'implies' character but does not
			// have another character with this function so ?rArr can be used for
			// 'implies' as ISOtech suggests -->
			htmlBuilder.put( "\u21D3", "&dArr;" ); // downwards double arrow, U+21D3 ISOamsa -->
			htmlBuilder.put( "\u21D4", "&hArr;" ); // left right double arrow,U+21D4 ISOamsa -->
			// <!-- Mathematical Operators -->
			htmlBuilder.put( "\u2200", "&forall;" ); // for all, U+2200 ISOtech -->
			htmlBuilder.put( "\u2202", "&part;" ); // partial differential, U+2202 ISOtech -->
			htmlBuilder.put( "\u2203", "&exist;" ); // there exists, U+2203 ISOtech -->
			htmlBuilder.put( "\u2205", "&empty;" ); // empty set = null set = diameter,U+2205 ISOamso -->
			htmlBuilder.put( "\u2207", "&nabla;" ); // nabla = backward difference,U+2207 ISOtech -->
			htmlBuilder.put( "\u2208", "&isin;" ); // element of, U+2208 ISOtech -->
			htmlBuilder.put( "\u2209", "&notin;" ); // not an element of, U+2209 ISOtech -->
			htmlBuilder.put( "\u220B", "&ni;" ); // contains as member, U+220B ISOtech -->
			// <!-- should there be a more memorable name than 'ni'? -->
			htmlBuilder.put( "\u220F", "&prod;" ); // n-ary product = product sign,U+220F ISOamsb -->
			// <!-- prod is NOT the same character as U+03A0 'greek capital letter pi'
			// though the same glyph might be used for both -->
			htmlBuilder.put( "\u2211", "&sum;" ); // n-ary summation, U+2211 ISOamsb -->
			// <!-- sum is NOT the same character as U+03A3 'greek capital letter sigma'
			// though the same glyph might be used for both -->
			htmlBuilder.put( "\u2212", "&minus;" ); // minus sign, U+2212 ISOtech -->
			htmlBuilder.put( "\u2217", "&lowast;" ); // asterisk operator, U+2217 ISOtech -->
			htmlBuilder.put( "\u221A", "&radic;" ); // square root = radical sign,U+221A ISOtech -->
			htmlBuilder.put( "\u221D", "&prop;" ); // proportional to, U+221D ISOtech -->
			htmlBuilder.put( "\u221E", "&infin;" ); // infinity, U+221E ISOtech -->
			htmlBuilder.put( "\u2220", "&ang;" ); // angle, U+2220 ISOamso -->
			htmlBuilder.put( "\u2227", "&and;" ); // logical and = wedge, U+2227 ISOtech -->
			htmlBuilder.put( "\u2228", "&or;" ); // logical or = vee, U+2228 ISOtech -->
			htmlBuilder.put( "\u2229", "&cap;" ); // intersection = cap, U+2229 ISOtech -->
			htmlBuilder.put( "\u222A", "&cup;" ); // union = cup, U+222A ISOtech -->
			htmlBuilder.put( "\u222B", "&int;" ); // integral, U+222B ISOtech -->
			htmlBuilder.put( "\u2234", "&there4;" ); // therefore, U+2234 ISOtech -->
			htmlBuilder.put( "\u223C", "&sim;" ); // tilde operator = varies with = similar to,U+223C ISOtech -->
			// <!-- tilde operator is NOT the same character as the tilde, U+007E,although
			// the same glyph might be used to represent both -->
			htmlBuilder.put( "\u2245", "&cong;" ); // approximately equal to, U+2245 ISOtech -->
			htmlBuilder.put( "\u2248", "&asymp;" ); // almost equal to = asymptotic to,U+2248 ISOamsr -->
			htmlBuilder.put( "\u2260", "&ne;" ); // not equal to, U+2260 ISOtech -->
			htmlBuilder.put( "\u2261", "&equiv;" ); // identical to, U+2261 ISOtech -->
			htmlBuilder.put( "\u2264", "&le;" ); // less-than or equal to, U+2264 ISOtech -->
			htmlBuilder.put( "\u2265", "&ge;" ); // greater-than or equal to,U+2265 ISOtech -->
			htmlBuilder.put( "\u2282", "&sub;" ); // subset of, U+2282 ISOtech -->
			htmlBuilder.put( "\u2283", "&sup;" ); // superset of, U+2283 ISOtech -->
			// <!-- note that nsup, 'not a superset of, U+2283' is not covered by the
			// Symbol font encoding and is not included. Should it be, for symmetry?
			// It is in ISOamsn --> <!ENTITY nsub", "8836",
			// not a subset of, U+2284 ISOamsn -->
			htmlBuilder.put( "\u2286", "&sube;" ); // subset of or equal to, U+2286 ISOtech -->
			htmlBuilder.put( "\u2287", "&supe;" ); // superset of or equal to,U+2287 ISOtech -->
			htmlBuilder.put( "\u2295", "&oplus;" ); // circled plus = direct sum,U+2295 ISOamsb -->
			htmlBuilder.put( "\u2297", "&otimes;" ); // circled times = vector product,U+2297 ISOamsb -->
			htmlBuilder.put( "\u22A5", "&perp;" ); // up tack = orthogonal to = perpendicular,U+22A5 ISOtech -->
			htmlBuilder.put( "\u22C5", "&sdot;" ); // dot operator, U+22C5 ISOamsb -->
			// <!-- dot operator is NOT the same character as U+00B7 middle dot -->
			// <!-- Miscellaneous Technical -->
			htmlBuilder.put( "\u2308", "&lceil;" ); // left ceiling = apl upstile,U+2308 ISOamsc -->
			htmlBuilder.put( "\u2309", "&rceil;" ); // right ceiling, U+2309 ISOamsc -->
			htmlBuilder.put( "\u230A", "&lfloor;" ); // left floor = apl downstile,U+230A ISOamsc -->
			htmlBuilder.put( "\u230B", "&rfloor;" ); // right floor, U+230B ISOamsc -->
			htmlBuilder.put( "\u2329", "&lang;" ); // left-pointing angle bracket = bra,U+2329 ISOtech -->
			// <!-- lang is NOT the same character as U+003C 'less than' or U+2039 'single left-pointing angle quotation
			// mark' -->
			htmlBuilder.put( "\u232A", "&rang;" ); // right-pointing angle bracket = ket,U+232A ISOtech -->
			// <!-- rang is NOT the same character as U+003E 'greater than' or U+203A
			// 'single right-pointing angle quotation mark' -->
			// <!-- Geometric Shapes -->
			htmlBuilder.put( "\u25CA", "&loz;" ); // lozenge, U+25CA ISOpub -->
			// <!-- Miscellaneous Symbols -->
			htmlBuilder.put( "\u2660", "&spades;" ); // black spade suit, U+2660 ISOpub -->
			// <!-- black here seems to mean filled as opposed to hollow -->
			htmlBuilder.put( "\u2663", "&clubs;" ); // black club suit = shamrock,U+2663 ISOpub -->
			htmlBuilder.put( "\u2665", "&hearts;" ); // black heart suit = valentine,U+2665 ISOpub -->
			htmlBuilder.put( "\u2666", "&diams;" ); // black diamond suit, U+2666 ISOpub -->

			// <!-- Latin Extended-A -->
			htmlBuilder.put( "\u0152", "&OElig;" ); // -- latin capital ligature OE,U+0152 ISOlat2 -->
			htmlBuilder.put( "\u0153", "&oelig;" ); // -- latin small ligature oe, U+0153 ISOlat2 -->
			// <!-- ligature is a misnomer, this is a separate character in some languages -->
			htmlBuilder.put( "\u0160", "&Scaron;" ); // -- latin capital letter S with caron,U+0160 ISOlat2 -->
			htmlBuilder.put( "\u0161", "&scaron;" ); // -- latin small letter s with caron,U+0161 ISOlat2 -->
			htmlBuilder.put( "\u0178", "&Yuml;" ); // -- latin capital letter Y with diaeresis,U+0178 ISOlat2 -->
			// <!-- Spacing Modifier Letters -->
			htmlBuilder.put( "\u02C6", "&circ;" ); // -- modifier letter circumflex accent,U+02C6 ISOpub -->
			htmlBuilder.put( "\u02DC", "&tilde;" ); // small tilde, U+02DC ISOdia -->
			// <!-- General Punctuation -->
			htmlBuilder.put( "\u2002", "&ensp;" ); // en space, U+2002 ISOpub -->
			htmlBuilder.put( "\u2003", "&emsp;" ); // em space, U+2003 ISOpub -->
			htmlBuilder.put( "\u2009", "&thinsp;" ); // thin space, U+2009 ISOpub -->
			htmlBuilder.put( "\u200C", "&zwnj;" ); // zero width non-joiner,U+200C NEW RFC 2070 -->
			htmlBuilder.put( "\u200D", "&zwj;" ); // zero width joiner, U+200D NEW RFC 2070 -->
			htmlBuilder.put( "\u200E", "&lrm;" ); // left-to-right mark, U+200E NEW RFC 2070 -->
			htmlBuilder.put( "\u200F", "&rlm;" ); // right-to-left mark, U+200F NEW RFC 2070 -->
			htmlBuilder.put( "\u2013", "&ndash;" ); // en dash, U+2013 ISOpub -->
			htmlBuilder.put( "\u2014", "&mdash;" ); // em dash, U+2014 ISOpub -->
			htmlBuilder.put( "\u2018", "&lsquo;" ); // left single quotation mark,U+2018 ISOnum -->
			htmlBuilder.put( "\u2019", "&rsquo;" ); // right single quotation mark,U+2019 ISOnum -->
			htmlBuilder.put( "\u201A", "&sbquo;" ); // single low-9 quotation mark, U+201A NEW -->
			htmlBuilder.put( "\u201C", "&ldquo;" ); // left double quotation mark,U+201C ISOnum -->
			htmlBuilder.put( "\u201D", "&rdquo;" ); // right double quotation mark,U+201D ISOnum -->
			htmlBuilder.put( "\u201E", "&bdquo;" ); // double low-9 quotation mark, U+201E NEW -->
			htmlBuilder.put( "\u2020", "&dagger;" ); // dagger, U+2020 ISOpub -->
			htmlBuilder.put( "\u2021", "&Dagger;" ); // double dagger, U+2021 ISOpub -->
			htmlBuilder.put( "\u2030", "&permil;" ); // per mille sign, U+2030 ISOtech -->
			htmlBuilder.put( "\u2039", "&lsaquo;" ); // single left-pointing angle quotation mark,U+2039 ISO proposed -->
			// <!-- lsaquo is proposed but not yet ISO standardized -->
			htmlBuilder.put( "\u203A", "&rsaquo;" ); // single right-pointing angle quotation mark,U+203A ISO proposed -->
			// <!-- rsaquo is proposed but not yet ISO standardized -->
			htmlBuilder.put( "\u20AC", "&euro;" ); // -- euro sign, U+20AC NEW -->
			HTML_ESCAPE = htmlBuilder.hashMap();

			UtilityMaps.MapBuilder<String, String> basicBuilder = UtilityMaps.builder();
			basicBuilder.put( "\"", "&quot;" ); // " - double-quote
			basicBuilder.put( "&", "&amp;" ); // & - ampersand
			basicBuilder.put( "<", "&lt;" ); // < - less-than
			basicBuilder.put( ">", "&gt;" ); // > - greater-than
			BASIC_ESCAPE = basicBuilder.hashMap();

			UtilityMaps.MapBuilder<String, String> ctrlCharBuilder = UtilityMaps.builder();
			basicBuilder.put( "\b", "\\b" );
			basicBuilder.put( "\n", "\\n" );
			basicBuilder.put( "\t", "\\t" );
			basicBuilder.put( "\f", "\\f" );
			basicBuilder.put( "\r", "\\r" );
			JAVA_CTRL_CHARS_ESCAPE = ctrlCharBuilder.hashMap();
		}

		public static EscapeTranslator APOS_ESCAPE()
		{
			return new EscapeTranslator( UtilityMaps.builder( "'", "&apos;" ).hashMap() );
		}

		public static EscapeTranslator APOS_UNESCAPE()
		{
			return new EscapeTranslator( UtilityMaps.builder( "&apos;", "'" ).hashMap() );
		}

		public static EscapeTranslator BASIC_ESCAPE()
		{
			return new EscapeTranslator( BASIC_ESCAPE );
		}

		public static EscapeTranslator BASIC_UNESCAPE()
		{
			return new EscapeTranslator( UtilityMaps.flipKeyValue( BASIC_ESCAPE ) );
		}

		public static EscapeTranslator HTML_ESCAPE()
		{
			return new EscapeTranslator( ISO8859_1_ESCAPE, HTML_ESCAPE );
		}

		public static EscapeTranslator HTML_UNESCAPE()
		{
			return new EscapeTranslator( UtilityMaps.flipKeyValue( ISO8859_1_ESCAPE ), UtilityMaps.flipKeyValue( HTML_ESCAPE ) );
		}

		public static EscapeTranslator ISO8859_1_ESCAPE()
		{
			return new EscapeTranslator( ISO8859_1_ESCAPE );
		}

		public static EscapeTranslator ISO8859_1_UNESCAPE()
		{
			return new EscapeTranslator( UtilityMaps.flipKeyValue( ISO8859_1_ESCAPE ) );
		}

		public static EscapeTranslator JAVA_CTRL_CHARS_ESCAPE()
		{
			return new EscapeTranslator( JAVA_CTRL_CHARS_ESCAPE );
		}

		public static EscapeTranslator JAVA_CTRL_CHARS_UNESCAPE()
		{
			return new EscapeTranslator( UtilityMaps.flipKeyValue( JAVA_CTRL_CHARS_ESCAPE ) );
		}

		private final Map<String, CharSequence> escapes = new TreeMap<>( Comparator.comparingInt( s -> Math.abs( ( s ).length() ) ) );
		private final int longest;

		private EscapeTranslator( Map<String, String>... escapeArray )
		{
			for ( Map<String, String> map : escapeArray )
				escapes.putAll( map );

			longest = UtilityMaps.first( escapes ).map( CharSequence::length ).orElse( 0 );
		}

		public final String translate( final CharSequence input )
		{
			if ( input == null )
				return null;

			StringBuilder output = new StringBuilder();

			int pos = 0;
			final int len = input.length();

			while ( pos < len )
			{
				int consumed = 0;
				int max = pos + longest > input.length() ? input.length() - pos : longest;
				for ( int i = max; i >= 0; i-- )
				{
					final CharSequence subSeq = input.subSequence( pos, pos + i );
					final CharSequence result = escapes.get( subSeq );
					if ( result != null )
					{
						output.append( result );
						consumed = i;
						break;
					}
				}

				if ( consumed == 0 )
				{
					final char[] c = Character.toChars( Character.codePointAt( input, pos ) );
					output.append( c );
					pos += c.length;
				}
				else
					for ( int pt = 0; pt < consumed; pt++ )
						pos += Character.charCount( Character.codePointAt( input, pos ) );
			}

			return output.toString();
		}
	}

	public static class StringChain
	{
		private String str;

		private StringChain( String str )
		{
			this.str = str;
		}

		public StringChain capitalizeWords( char delimiter )
		{
			str = UtilityStrings.capitalizeWords( str, delimiter );
			return this;
		}

		public StringChain capitalizeWords()
		{
			str = UtilityStrings.capitalizeWords( str );
			return this;
		}

		public StringChain capitalizeWordsFully( char delimiter )
		{
			str = UtilityStrings.capitalizeWordsFully( str, delimiter );
			return this;
		}

		public StringChain capitalizeWordsFully()
		{
			str = UtilityStrings.capitalizeWordsFully( str );
			return this;
		}

		public StringChain escape()
		{
			for ( String s : new String[] {"\\", "+", ".", "?", "*", "[", "]", "^", "$", "(", ")", "{", "}", "=", "!", "<", ">", "|", ":", "-"} )
				str = str.replace( s, "\\" + s );
			return this;
		}

		public String get()
		{
			return str;
		}

		public StringChain lcFirst()
		{
			str = str.substring( 0, 1 ).toLowerCase() + str.substring( 1 );
			return this;
		}

		public StringChain removeInvalidChars()
		{
			return replaceInvalidChars( "" );
		}

		public StringChain removeLetters()
		{
			return replaceLetters( "" );
		}

		public StringChain removeLettersLower()
		{
			return replaceLettersLower( "" );
		}

		public StringChain removeLettersUpper()
		{
			return replaceLettersUpper( "" );
		}

		public StringChain removeNumbers()
		{
			return replaceNumbers( "" );
		}

		public StringChain removeSpecial()
		{
			return replaceSpecial( "" );
		}

		public StringChain removeWhitespace()
		{
			return replaceWhitespace( "" );
		}

		public StringChain repeat( int cnt )
		{
			str = UtilityStrings.repeat( str, cnt );
			return this;
		}

		public StringChain replace( String orig, String replace )
		{
			str = Pattern.compile( orig, Pattern.LITERAL ).matcher( str ).replaceAll( replace );
			return this;
		}

		public StringChain replaceInvalidChars( String replace )
		{
			str = str.replaceAll( "[^a-zA-Z0-9!#$%&'*+-/=?^_`{|}~@\\. ]", replace );
			return this;
		}

		public StringChain replaceLetters( String replace )
		{
			str = str.replaceAll( "[a-zA-Z]", replace );
			return this;
		}

		public StringChain replaceLettersLower( String replace )
		{
			str = str.replaceAll( "[a-z]", replace );
			return this;
		}

		public StringChain replaceLettersUpper( String replace )
		{
			str = str.replaceAll( "[A-Z]", replace );
			return this;
		}

		public StringChain replaceNumbers( String replace )
		{
			str = str.replaceAll( "\\d", replace );
			return this;
		}

		public StringChain replaceRegex( String orig, String replace )
		{
			str = str.replaceAll( orig, replace );
			return this;
		}

		public StringChain replaceSpecial( String replace )
		{
			str = str.replaceAll( "\\W", replace );
			return this;
		}

		public StringChain replaceWhitespace( String replace )
		{
			str = str.replaceAll( "\\s", replace );
			return this;
		}

		public StringChain slugify()
		{
			str = UtilityStrings.slugify( str );
			return this;
		}

		public StringChain toCamelCase()
		{
			str = UtilityStrings.lcFirst( UtilityStrings.toStudlyCase( str ) );
			return this;
		}

		public StringChain toLowercase()
		{
			str = str.toLowerCase();
			return this;
		}

		public StringChain toStudlyCase()
		{
			str = UtilityStrings.capitalizeWordsFully( str.replaceAll( "-_", " " ) ).replaceAll( " ", "" );
			return this;
		}

		public StringChain toUppercase()
		{
			str = str.toUpperCase();
			return this;
		}

		public StringChain trimAll( char character )
		{
			String normalizedText = trimStart( str, character );
			str = UtilityStrings.trimEnd( normalizedText, character );
			return this;
		}

		public StringChain trimAll()
		{
			return trimRegex( "\\s" );
		}

		public StringChain trimEnd( char character )
		{
			str = UtilityStrings.trimEnd( str, character );
			return this;
		}

		public StringChain trimFront( char character )
		{
			str = trimStart( str, character );
			return this;
		}

		public StringChain trimRegex( String regex )
		{
			str = trimAllRegex( str, regex );
			return this;
		}

		public StringChain wrap( char wrap )
		{
			str = String.format( "%s%s%s", wrap, str, wrap );
			return this;
		}
	}

	/**
	 * Mirrored from {@see java.lang.StringCoding} for public access
	 */
	public static class StringCoding
	{
		static char[] decode( byte[] ba, int off, int len )
		{
			return invokeMethod( "decode", ba, off, len );
		}

		static char[] decode( Charset cs, byte[] ba, int off, int len )
		{
			return invokeMethod( "decode", cs, ba, off, len );
		}

		static char[] decode( String charsetName, byte[] ba, int off, int len )
		{
			return invokeMethod( "decode", charsetName, ba, off, len );
		}

		static byte[] encode( char[] ca, int off, int len )
		{
			return invokeMethod( "decode", ca, off, len );
		}

		static byte[] encode( Charset cs, char[] ca, int off, int len )
		{
			return invokeMethod( "decode", cs, ca, off, len );
		}

		static byte[] encode( String charsetName, char[] ca, int off, int len )
		{
			return invokeMethod( "decode", charsetName, ca, off, len );
		}

		private static <R> R invokeMethod( String methodName, Object... args )
		{
			return UtilityObjects.invokeStaticMethod( "java.lang.StringCoding", methodName, args );
		}
	}
}
