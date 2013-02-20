
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;

import java.util.HashSet;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;



public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		// String fileContents = IOUtils.slurpFile("input.txt");

		/*
		PDDocument doc = PDDocument.load(pdfFile);  
        PDFTextStripper stripper = new PDFTextStripper();  
        String fileContents = stripper.getText(doc);
        */
		
		String classifierFile = args[0];
        
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(classifierFile);
		
		while( true )
		{
			
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String text = "";
	        
	        while(true)
	        {
	        	String nextLine = br.readLine();
	        	if( nextLine.equals("--END--") )
	        		break;
	        	text += nextLine + "\n";
	        }
	        
			HashSet<String> tokens = new HashSet<String>();
			addTokens(classifier, text, tokens);
				      
			for( Object token : tokens.toArray() )
			{
				System.out.println(token);
			}
			System.out.println("--END--");
		
		}
		
	}
	
	private static void addTokens(AbstractSequenceClassifier<CoreLabel> classifier, String text, HashSet<String> output)
	{
	    int lastCharacterOffset = -10;
	    String currentToken = "";
	    
        for (List<CoreLabel> lcl : classifier.classify(text)) {
        	
            for (CoreLabel cl : lcl) {
            	
            	if( cl.get(AnswerAnnotation.class).toString().equals("ORGANIZATION") ||
            		cl.get(AnswerAnnotation.class).toString().equals("PERSON")
    			)
            	{
            	  int delta = cl.get(CharacterOffsetBeginAnnotation.class) - lastCharacterOffset;
            	  
            	  if( delta > 2 )
            	  {
            		  if( !currentToken.equals("") )
            		  {
            			  output.add(currentToken);
            		  }
            		  
            		  currentToken = cl.get(TextAnnotation.class);
            	  }
            	  else
            	  {
            		  if( delta == 1 ) {
            			  currentToken = currentToken + " ";
            		  }
            		  currentToken = currentToken + cl.get(TextAnnotation.class);
            	  }
            	  
        		  lastCharacterOffset = cl.get(CharacterOffsetEndAnnotation.class);
            	}
            }
        }
	      
	}
	
	/*
    private static void doIt2( String inputFile, String outputFile, HashSet<String> tokensToFind) throws Exception
    {
		PDFDoc doc = new PDFDoc(inputFile);
		doc.initSecurityHandler();
		
		TextSearch ts = new TextSearch();
		ts.begin( doc, "Pepito", 0, 1, 2);
		TextSearchResult res = ts.run();
		
		res.
		
		ts.
		
		ContentReplacer replacer = new ContentReplacer();
		for( Object token : tokensToFind)
		{
			replacer.addString(token.toString(), "XXXXX");
		}
		
		for( int i=0; i<doc.getPageCount(); i++ )
		{
			Page page = doc.getPage(i);
			replacer.process(page);
		}

		doc.save(outputFile, SDFDoc.e_remove_unused, null);    	
    }
    */
	
	/*
	private static void doIt( String inputFile, String outputFile, HashSet<String> tokensToFind)
            throws IOException, COSVisitorException
        {
            // the document
            PDDocument doc = null;
            try
            {
                doc = PDDocument.load( inputFile );
                List pages = doc.getDocumentCatalog().getAllPages();
                for( int i=0; i<pages.size(); i++ )
                {
                    PDPage page = (PDPage)pages.get( i );
                    PDStream contents = page.getContents();
                    PDFStreamParser parser = new PDFStreamParser(contents.getStream() );
                    parser.parse();
                    List tokens = parser.getTokens();
                    for( int j=0; j<tokens.size(); j++ )
                    {
                        Object next = tokens.get( j );
                        if( next instanceof PDFOperator )
                        {
                            PDFOperator op = (PDFOperator)next;
                            //Tj and TJ are the two operators that display
                            //strings in a PDF
                            if( op.getOperation().equals( "Tj" ) )
                            {
                                //Tj takes one operator and that is the string
                                //to display so lets update that operator
                                COSString previous = (COSString)tokens.get( j-1 );
                                String string = previous.getString();
                                for( Object tokenToFind : tokensToFind )
                                {
                                	string = string.replaceAll( tokenToFind.toString(), "XXXXX" );
                                }
                                previous.reset();
                                previous.append( string.getBytes("ISO-8859-1") );
                            }
                            else if( op.getOperation().equals( "TJ" ) )
                            {
                                COSArray previous = (COSArray)tokens.get( j-1 );
                                for( int k=0; k<previous.size(); k++ )
                                {
                                    Object arrElement = previous.getObject( k );
                                    if( arrElement instanceof COSString )
                                    {
                                        COSString cosString = (COSString)arrElement;
                                        String string = cosString.getString();
                                        for( Object tokenToFind : tokensToFind )
                                        {
                                        	string = string.replaceAll( tokenToFind.toString(), "XXXXX" );
                                        }
                                        cosString.reset();
                                        cosString.append( string.getBytes("ISO-8859-1") );
                                    }
                                }
                            }
                        }
                    }
                    //now that the tokens are updated we will replace the
                    //page content stream.
                    PDStream updatedStream = new PDStream(doc);
                    OutputStream out = updatedStream.createOutputStream();
                    ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
                    tokenWriter.writeTokens( tokens );
                    page.setContents( updatedStream );
                }
                doc.save( outputFile );
            }
            finally
            {
                if( doc != null )
                {
                    doc.close();
                }
            }
        }
        */
	
	

}
	