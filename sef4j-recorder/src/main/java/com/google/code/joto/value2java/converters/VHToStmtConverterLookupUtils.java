package com.google.code.joto.value2java.converters;

import com.google.code.joto.value2java.VHToStmtConverterLookup;

public class VHToStmtConverterLookupUtils {

	// -------------------------------------------------------------------------
	
	public static void registerDefaultConverters(VHToStmtConverterLookup p) {
		PrimitiveWrapperVHToStmtConverter.registerDefaultConverters(p);
		JavaLangVHToStmtConverters.registerDefaultConverters(p); // TODO use default ClassLoader...
		JavaUtilConverters.registerDefaultConverters(p);
		
//        final ReflectionConverter reflectionConverter = 
//            new ReflectionConverter(mapper, reflectionProvider);
//        registerConverter(reflectionConverter, PRIORITY_VERY_LOW);
//
//        registerConverter(new SerializableConverter(mapper, reflectionProvider), PRIORITY_LOW);
//        registerConverter(new ExternalizableConverter(mapper), PRIORITY_LOW);
//
//        registerConverter(new StringBufferConverter(), PRIORITY_NORMAL);
//        registerConverter(new DateConverter(), PRIORITY_NORMAL);
//        registerConverter(new BitSetConverter(), PRIORITY_NORMAL);
//        registerConverter(new URLConverter(), PRIORITY_NORMAL);
//        registerConverter(new BigIntegerConverter(), PRIORITY_NORMAL);
//        registerConverter(new BigDecimalConverter(), PRIORITY_NORMAL);
//
//        registerConverter(new ArrayConverter(mapper), PRIORITY_NORMAL);
//        registerConverter(new CharArrayConverter(), PRIORITY_NORMAL);
//        registerConverter(new CollectionConverter(mapper), PRIORITY_NORMAL);
//        registerConverter(new MapConverter(mapper), PRIORITY_NORMAL);
//        registerConverter(new TreeMapConverter(mapper), PRIORITY_NORMAL);
//        registerConverter(new TreeSetConverter(mapper), PRIORITY_NORMAL);
//        registerConverter(new PropertiesConverter(), PRIORITY_NORMAL);
//        registerConverter(new EncodedByteArrayConverter(), PRIORITY_NORMAL);
//
//        registerConverter(new FileConverter(), PRIORITY_NORMAL);
//        if(jvm.supportsSQL()) {
//	        registerConverter(new SqlTimestampConverter(), PRIORITY_NORMAL);
//	        registerConverter(new SqlTimeConverter(), PRIORITY_NORMAL);
//	        registerConverter(new SqlDateConverter(), PRIORITY_NORMAL);
//        }
//        registerConverter(new DynamicProxyConverter(mapper, classLoaderReference), PRIORITY_NORMAL);

//        if(jvm.supportsAWT()) {
//	        registerConverter(new FontConverter(), PRIORITY_NORMAL);
//	        registerConverter(new ColorConverter(), PRIORITY_NORMAL);
//	        registerConverter(new TextAttributeConverter(), PRIORITY_NORMAL);
//        }
//        if(jvm.supportsSwing()) {
//            registerConverter(new LookAndFeelConverter(mapper, reflectionProvider), PRIORITY_NORMAL);
//        }
//        registerConverter(new LocaleConverter(), PRIORITY_NORMAL);
//        registerConverter(new GregorianCalendarConverter(), PRIORITY_NORMAL);
//
//        if (JVM.is14()) {
//            // late bound converters - allows XStream to be compiled on earlier JDKs
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.extended.SubjectConverter",
//                    PRIORITY_NORMAL, new Class[]{Mapper.class}, new Object[]{mapper});
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.extended.ThrowableConverter",
//                    PRIORITY_NORMAL, new Class[]{Converter.class},
//                    new Object[]{reflectionConverter});
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.extended.StackTraceElementConverter",
//                    PRIORITY_NORMAL, null, null);
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.extended.CurrencyConverter",
//                    PRIORITY_NORMAL, null, null);
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.extended.RegexPatternConverter",
//                    PRIORITY_NORMAL, new Class[]{Converter.class},
//                    new Object[]{reflectionConverter});
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.extended.CharsetConverter",
//                    PRIORITY_NORMAL, null, null);
//        }
//
//        if (JVM.is15()) {
//            // late bound converters - allows XStream to be compiled on earlier JDKs
//            dynamicallyRegisterConverter(
//                "com.thoughtworks.xstream.converters.extended.DurationConverter",
//                PRIORITY_NORMAL, null, null);
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.enums.EnumConverter", PRIORITY_NORMAL,
//                    null, null);
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.enums.EnumSetConverter", PRIORITY_NORMAL,
//                    new Class[]{Mapper.class}, new Object[]{mapper});
//            dynamicallyRegisterConverter(
//                    "com.thoughtworks.xstream.converters.enums.EnumMapConverter", PRIORITY_NORMAL,
//                    new Class[]{Mapper.class}, new Object[]{mapper});
//            dynamicallyRegisterConverter(
//                "com.thoughtworks.xstream.converters.basic.StringBuilderConverter", PRIORITY_NORMAL,
//                null, null);
//            dynamicallyRegisterConverter(
//                "com.thoughtworks.xstream.converters.basic.UUIDConverter", PRIORITY_NORMAL,
//                null, null);
//        }
//
//        registerConverter(new SelfStreamingInstanceChecker(reflectionConverter, this), PRIORITY_NORMAL);
	
	}

	
}
