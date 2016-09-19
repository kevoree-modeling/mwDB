// Generated from /Users/ludovicmouline/Documents/Thesis/MwDB/plugins/model/dsl/src/main/antlr4/MetaModel.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MetaModelLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, STRING=22, IDENT=23, TYPE_NAME=24, 
		NUMBER=25, WS=26, SL_COMMENT=27;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "ESC", "UNICODE", "HEX", "STRING", 
		"IDENT", "TYPE_NAME", "NUMBER", "WS", "SL_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'index'", "':'", "'{'", "'}'", "','", "'enum'", "'class'", "'extends'", 
		"'with'", "'using'", "'from'", "'learned'", "'derived'", "'global'", "'String'", 
		"'Double'", "'Long'", "'Integer'", "'Boolean'", "'att'", "'rel'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "STRING", 
		"IDENT", "TYPE_NAME", "NUMBER", "WS", "SL_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MetaModelLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MetaModel.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\35\u0105\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n"+
		"\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3\27\5\27\u00b6\n\27\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\32\7\32\u00c3\n\32\f\32\16"+
		"\32\u00c6\13\32\3\32\3\32\3\32\3\32\7\32\u00cc\n\32\f\32\16\32\u00cf\13"+
		"\32\3\32\5\32\u00d2\n\32\3\33\3\33\7\33\u00d6\n\33\f\33\16\33\u00d9\13"+
		"\33\3\34\3\34\7\34\u00dd\n\34\f\34\16\34\u00e0\13\34\3\35\5\35\u00e3\n"+
		"\35\3\35\6\35\u00e6\n\35\r\35\16\35\u00e7\3\35\5\35\u00eb\n\35\3\35\7"+
		"\35\u00ee\n\35\f\35\16\35\u00f1\13\35\3\36\6\36\u00f4\n\36\r\36\16\36"+
		"\u00f5\3\36\5\36\u00f9\n\36\3\36\3\36\3\37\3\37\3\37\3\37\7\37\u0101\n"+
		"\37\f\37\16\37\u0104\13\37\2\2 \3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\2/\2\61"+
		"\2\63\30\65\31\67\329\33;\34=\35\3\2\f\n\2$$\61\61^^ddhhppttvv\5\2\62"+
		";CHch\4\2$$^^\5\2C\\aac|\6\2\62;C\\aac|\7\2\60\60\62;C\\aac|\3\2//\3\2"+
		"\62;\5\2\13\f\17\17\"\"\4\2\f\f\17\17\u0110\2\3\3\2\2\2\2\5\3\2\2\2\2"+
		"\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2"+
		"\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2"+
		"\2\2)\3\2\2\2\2+\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\3?\3\2\2\2\5E\3\2\2\2\7G\3\2\2\2\tI\3\2\2\2"+
		"\13K\3\2\2\2\rM\3\2\2\2\17R\3\2\2\2\21X\3\2\2\2\23`\3\2\2\2\25e\3\2\2"+
		"\2\27k\3\2\2\2\31p\3\2\2\2\33x\3\2\2\2\35\u0080\3\2\2\2\37\u0087\3\2\2"+
		"\2!\u008e\3\2\2\2#\u0095\3\2\2\2%\u009a\3\2\2\2\'\u00a2\3\2\2\2)\u00aa"+
		"\3\2\2\2+\u00ae\3\2\2\2-\u00b2\3\2\2\2/\u00b7\3\2\2\2\61\u00bd\3\2\2\2"+
		"\63\u00d1\3\2\2\2\65\u00d3\3\2\2\2\67\u00da\3\2\2\29\u00e2\3\2\2\2;\u00f8"+
		"\3\2\2\2=\u00fc\3\2\2\2?@\7k\2\2@A\7p\2\2AB\7f\2\2BC\7g\2\2CD\7z\2\2D"+
		"\4\3\2\2\2EF\7<\2\2F\6\3\2\2\2GH\7}\2\2H\b\3\2\2\2IJ\7\177\2\2J\n\3\2"+
		"\2\2KL\7.\2\2L\f\3\2\2\2MN\7g\2\2NO\7p\2\2OP\7w\2\2PQ\7o\2\2Q\16\3\2\2"+
		"\2RS\7e\2\2ST\7n\2\2TU\7c\2\2UV\7u\2\2VW\7u\2\2W\20\3\2\2\2XY\7g\2\2Y"+
		"Z\7z\2\2Z[\7v\2\2[\\\7g\2\2\\]\7p\2\2]^\7f\2\2^_\7u\2\2_\22\3\2\2\2`a"+
		"\7y\2\2ab\7k\2\2bc\7v\2\2cd\7j\2\2d\24\3\2\2\2ef\7w\2\2fg\7u\2\2gh\7k"+
		"\2\2hi\7p\2\2ij\7i\2\2j\26\3\2\2\2kl\7h\2\2lm\7t\2\2mn\7q\2\2no\7o\2\2"+
		"o\30\3\2\2\2pq\7n\2\2qr\7g\2\2rs\7c\2\2st\7t\2\2tu\7p\2\2uv\7g\2\2vw\7"+
		"f\2\2w\32\3\2\2\2xy\7f\2\2yz\7g\2\2z{\7t\2\2{|\7k\2\2|}\7x\2\2}~\7g\2"+
		"\2~\177\7f\2\2\177\34\3\2\2\2\u0080\u0081\7i\2\2\u0081\u0082\7n\2\2\u0082"+
		"\u0083\7q\2\2\u0083\u0084\7d\2\2\u0084\u0085\7c\2\2\u0085\u0086\7n\2\2"+
		"\u0086\36\3\2\2\2\u0087\u0088\7U\2\2\u0088\u0089\7v\2\2\u0089\u008a\7"+
		"t\2\2\u008a\u008b\7k\2\2\u008b\u008c\7p\2\2\u008c\u008d\7i\2\2\u008d "+
		"\3\2\2\2\u008e\u008f\7F\2\2\u008f\u0090\7q\2\2\u0090\u0091\7w\2\2\u0091"+
		"\u0092\7d\2\2\u0092\u0093\7n\2\2\u0093\u0094\7g\2\2\u0094\"\3\2\2\2\u0095"+
		"\u0096\7N\2\2\u0096\u0097\7q\2\2\u0097\u0098\7p\2\2\u0098\u0099\7i\2\2"+
		"\u0099$\3\2\2\2\u009a\u009b\7K\2\2\u009b\u009c\7p\2\2\u009c\u009d\7v\2"+
		"\2\u009d\u009e\7g\2\2\u009e\u009f\7i\2\2\u009f\u00a0\7g\2\2\u00a0\u00a1"+
		"\7t\2\2\u00a1&\3\2\2\2\u00a2\u00a3\7D\2\2\u00a3\u00a4\7q\2\2\u00a4\u00a5"+
		"\7q\2\2\u00a5\u00a6\7n\2\2\u00a6\u00a7\7g\2\2\u00a7\u00a8\7c\2\2\u00a8"+
		"\u00a9\7p\2\2\u00a9(\3\2\2\2\u00aa\u00ab\7c\2\2\u00ab\u00ac\7v\2\2\u00ac"+
		"\u00ad\7v\2\2\u00ad*\3\2\2\2\u00ae\u00af\7t\2\2\u00af\u00b0\7g\2\2\u00b0"+
		"\u00b1\7n\2\2\u00b1,\3\2\2\2\u00b2\u00b5\7^\2\2\u00b3\u00b6\t\2\2\2\u00b4"+
		"\u00b6\5/\30\2\u00b5\u00b3\3\2\2\2\u00b5\u00b4\3\2\2\2\u00b6.\3\2\2\2"+
		"\u00b7\u00b8\7w\2\2\u00b8\u00b9\5\61\31\2\u00b9\u00ba\5\61\31\2\u00ba"+
		"\u00bb\5\61\31\2\u00bb\u00bc\5\61\31\2\u00bc\60\3\2\2\2\u00bd\u00be\t"+
		"\3\2\2\u00be\62\3\2\2\2\u00bf\u00c4\7$\2\2\u00c0\u00c3\5-\27\2\u00c1\u00c3"+
		"\n\4\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c4\3\2"+
		"\2\2\u00c7\u00d2\7$\2\2\u00c8\u00cd\7)\2\2\u00c9\u00cc\5-\27\2\u00ca\u00cc"+
		"\n\4\2\2\u00cb\u00c9\3\2\2\2\u00cb\u00ca\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd"+
		"\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d0\3\2\2\2\u00cf\u00cd\3\2"+
		"\2\2\u00d0\u00d2\7)\2\2\u00d1\u00bf\3\2\2\2\u00d1\u00c8\3\2\2\2\u00d2"+
		"\64\3\2\2\2\u00d3\u00d7\t\5\2\2\u00d4\u00d6\t\6\2\2\u00d5\u00d4\3\2\2"+
		"\2\u00d6\u00d9\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\66"+
		"\3\2\2\2\u00d9\u00d7\3\2\2\2\u00da\u00de\t\5\2\2\u00db\u00dd\t\7\2\2\u00dc"+
		"\u00db\3\2\2\2\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2"+
		"\2\2\u00df8\3\2\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e3\t\b\2\2\u00e2\u00e1"+
		"\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e5\3\2\2\2\u00e4\u00e6\t\t\2\2\u00e5"+
		"\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2"+
		"\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00eb\7\60\2\2\u00ea\u00e9\3\2\2\2\u00ea"+
		"\u00eb\3\2\2\2\u00eb\u00ef\3\2\2\2\u00ec\u00ee\t\t\2\2\u00ed\u00ec\3\2"+
		"\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0"+
		":\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2\u00f4\t\n\2\2\u00f3\u00f2\3\2\2\2"+
		"\u00f4\u00f5\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f9"+
		"\3\2\2\2\u00f7\u00f9\5=\37\2\u00f8\u00f3\3\2\2\2\u00f8\u00f7\3\2\2\2\u00f9"+
		"\u00fa\3\2\2\2\u00fa\u00fb\b\36\2\2\u00fb<\3\2\2\2\u00fc\u00fd\7\61\2"+
		"\2\u00fd\u00fe\7\61\2\2\u00fe\u0102\3\2\2\2\u00ff\u0101\n\13\2\2\u0100"+
		"\u00ff\3\2\2\2\u0101\u0104\3\2\2\2\u0102\u0100\3\2\2\2\u0102\u0103\3\2"+
		"\2\2\u0103>\3\2\2\2\u0104\u0102\3\2\2\2\22\2\u00b5\u00c2\u00c4\u00cb\u00cd"+
		"\u00d1\u00d7\u00de\u00e2\u00e7\u00ea\u00ef\u00f5\u00f8\u0102\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}