/*
  Algoritmo que converte a especificação de um workflow no formato XML para o formato .smv. 
 Como saída é gerado um arquivo que quando submetido a um model cheker específico, verifica
  a existência de deadlocks.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Principal {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String newLine 			= System.getProperty("line.separator");
		String resultado 		= "";
		String estados 			=  ""			;
		String relacoes 		= ""			+ newLine;
		String estadosRelacoes 	= "Estados + Relações:"	+ newLine;

		try {
			//Objetos para fazer o parsing
			DocumentBuilderFactory 	factory = 	DocumentBuilderFactory.newInstance();
			DocumentBuilder			builder	=	factory.newDocumentBuilder();
			
			//Realizando o parsing e colocando as atividades do workflow dentro de uma lista
			Document arquivoXML  = builder.parse("C:\\scicumulus.xml");
			NodeList listaDeAtividades 	= arquivoXML.getElementsByTagName("activity");
			NodeList listaDeRelacoe		= arquivoXML.getElementsByTagName("relation");
			
			int tamLista = listaDeAtividades.getLength();
			for (int i = 0; i < tamLista; i++) {
				//coloca as atividades em uma lista e mostra seu nome
				
				relacoes = relacoes + "dependencia"+i+": {";
				Node noAtividade = (Node) listaDeAtividades.item(i);
				if (noAtividade.getNodeType() == Node.ELEMENT_NODE) {
					org.w3c.dom.Element elementoAtividade = (org.w3c.dom.Element) noAtividade;
					String desc = elementoAtividade.getAttribute("tag");
					
					estados 		= estados + desc;
					
					relacoes = relacoes + desc;
					
					if (i < tamLista-1) {
						estados = estados + ", ";
					}
					estadosRelacoes = estadosRelacoes + desc;
					
					NodeList listaDeSubTagsDaAtividade = elementoAtividade.getChildNodes();
					int tamListaSubTags = listaDeSubTagsDaAtividade.getLength();
					
					for (int j = 0; j < tamListaSubTags; j++) {
						
						Node noSubTag = listaDeSubTagsDaAtividade.item(j);
						
						if (noSubTag.getNodeType() == Node.ELEMENT_NODE) {
							org.w3c.dom.Element subTag = (org.w3c.dom.Element) noSubTag;
							
							switch (subTag.getTagName()) {
							
							case "relation":
								String tipoRelacao 	= subTag.getAttribute("reltype");
								String dependeDe 	= subTag.getAttribute("dependency");
								
								if (dependeDe != "") {
									relacoes		= relacoes + ", "+ dependeDe;
								}
								estadosRelacoes = estadosRelacoes +" "+ tipoRelacao +" "+ dependeDe;
								
								break;

							default:
								break;
							}
						}
					}
					
					
					estadosRelacoes = estadosRelacoes + newLine;

				}
				
				relacoes = relacoes +"};"+newLine;
			}
			
			/*System.out.println("");
			System.out.println("\n**Relações dos estados do workflow**");
			int tamListaRelacoes = listaDeRelacoe.getLength();
			for (int i = 0; i < tamListaRelacoes; i++) {
				
				//coloca as relações em uma lista e mostra sua dependência e tipo i/o
				Node noRelacao = (Node) listaDeRelacoe.item(i);
				if (noRelacao.getNodeType() == Node.ELEMENT_NODE) {
					org.w3c.dom.Element elementoRelacao = (org.w3c.dom.Element) noRelacao;
					System.out.print("Relação: R"+i+", ");
					
					String nome = elementoRelacao.getAttribute("name");
					String dependencia = elementoRelacao.getAttribute("dependency");
					String tipo = elementoRelacao.getAttribute("reltype");
					
					
					if (dependencia == "") {
						dependencia = " null ";
					}
					relacoes =  dependencia + tipo + newLine ;
					System.out.print("nome: "+nome+"");
					System.out.print("dependência: "+dependencia+"");	
					System.out.print("tipo: "+tipo+"\n");	
					
					//NodeList listaDeAtributos = elementoAtividade.getChildNodes();
					//System.out.println("R"+i+", ");
				}
			}*/
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			//adicionar o arquivoDeSaida.smv no diretório C:\\
			
			String nuxmv = "MODULE main\r\n" + 
					"VAR\r\n"
					+ "state: {" +estados+ "};"
					+ relacoes+
					"\nINIT"+
					"TRANS"+
					"INVARSPEC";
			
			Path diretorio = Paths.get("C:\\arquivoDeSaida.smv");
			resultado = estadosRelacoes + estados + relacoes;
			byte[] gravar = nuxmv.getBytes();
			Files.write(diretorio, gravar);
			
			//System.out.println(estados);
			//System.out.println(relacoes);
			//System.out.println(estadosRelacoes);
			System.out.println(nuxmv);
			System.out.println(estadosRelacoes);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
