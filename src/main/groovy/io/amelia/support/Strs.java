/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.support;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.amelia.lang.APINotice;

public class Strs
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
		if ( Objs.isEmpty( str ) )
			return str;

		Objs.notNull( delimiter );

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
		if ( Objs.isEmpty( str ) )
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
		Objs.notNull( token, "Search token cannot be null" );
		Objs.notNull( collection, "Collection cannot be null" );
		Objs.notNull( originals, "Originals cannot be null" );

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
		return args.entrySet().stream().map( e -> e.getKey() + keyValueSeparator + Objs.castToString( e.getValue() ) ).collect( Collectors.joining( glue ) );
	}

	public static String join( @Nonnull Map<String, ?> args, @Nonnull String glue, @Nonnull String keyValueSeparator, @Nonnull String nullValue )
	{
		return args.entrySet().stream().map( e -> e.getKey() + keyValueSeparator + ( e.getValue() == null ? nullValue : Objs.castToString( e.getValue() ) ) ).collect( Collectors.joining( glue ) );
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
		Objs.notFalse( str.length() == len, "String is lessThan or greatThan the required string length. {required=" + len + ",got=" + str.length() + "}" );
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
		Objs.notEmpty( seed );

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
		if ( Objs.isEmpty( str ) )
			return Stream.empty();
		return Arrays.stream( str.split( delimiter, limit ) );
	}

	public static Stream<String> split( @Nonnull String str, @Nonnull String delimiter )
	{
		if ( Objs.isEmpty( str ) )
			return Stream.empty();
		return Arrays.stream( str.split( delimiter ) );
	}

	public static Stream<String> split( @Nonnull String str, @Nonnull Pattern delimiter )
	{
		if ( Objs.isEmpty( str ) )
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
		return strings.stream().filter( v -> !Objs.isNull( v ) ).map( String::toLowerCase ).collect( Collectors.toList() );
	}

	public static Set<String> toLowerCase( Set<String> strings )
	{
		return strings.stream().filter( v -> !Objs.isNull( v ) ).map( String::toLowerCase ).collect( Collectors.toSet() );
	}

	public static String[] toLowerCase( String[] strings )
	{
		return Arrays.stream( strings ).filter( v -> !Objs.isNull( v ) ).map( String::toLowerCase ).toArray( String[]::new );
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

		while ( io.amelia.support.Arrs.contains( characters, normalizedText.charAt( index ) ) )
			if ( --index < 0 )
				return "";
		return normalizedText.substring( 0, index + 1 ).trim();
	}

	public static String trimEnd( @Nullable String text, String substr )
	{
		if ( Objs.isEmpty( text ) )
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
		if ( Objs.isEmpty( text ) )
			return text;

		String normalizedText = text.trim();
		int index = -1;

		do
			index++;
		while ( index < normalizedText.length() && io.amelia.support.Arrs.contains( characters, normalizedText.charAt( index ) ) );

		return normalizedText.substring( index ).trim();
	}

	public static String trimStart( @Nullable String text, String substr )
	{
		if ( Objs.isEmpty( text ) )
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

	private Strs()
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
			return Objs.invokeStaticMethod( "java.lang.ConditionalSpecialCasing", methodName, args );
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

	public static class StringChain
	{
		private String str;

		private StringChain( String str )
		{
			this.str = str;
		}

		public StringChain capitalizeWords( char delimiter )
		{
			str = io.amelia.support.Strs.capitalizeWords( str, delimiter );
			return this;
		}

		public StringChain capitalizeWords()
		{
			str = io.amelia.support.Strs.capitalizeWords( str );
			return this;
		}

		public StringChain capitalizeWordsFully( char delimiter )
		{
			str = io.amelia.support.Strs.capitalizeWordsFully( str, delimiter );
			return this;
		}

		public StringChain capitalizeWordsFully()
		{
			str = io.amelia.support.Strs.capitalizeWordsFully( str );
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
			str = io.amelia.support.Strs.repeat( str, cnt );
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
			str = io.amelia.support.Strs.slugify( str );
			return this;
		}

		public StringChain toCamelCase()
		{
			str = io.amelia.support.Strs.lcFirst( io.amelia.support.Strs.toStudlyCase( str ) );
			return this;
		}

		public StringChain toLowercase()
		{
			str = str.toLowerCase();
			return this;
		}

		public StringChain toStudlyCase()
		{
			str = io.amelia.support.Strs.capitalizeWordsFully( str.replaceAll( "-_", " " ) ).replaceAll( " ", "" );
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
			str = io.amelia.support.Strs.trimEnd( normalizedText, character );
			return this;
		}

		public StringChain trimAll()
		{
			return trimRegex( "\\s" );
		}

		public StringChain trimEnd( char character )
		{
			str = io.amelia.support.Strs.trimEnd( str, character );
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
			return Objs.invokeStaticMethod( "java.lang.StringCoding", methodName, args );
		}
	}
}
