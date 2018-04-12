import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	// Paramêtros
	// args[0] = nome do arquivo
	// args[1] = k grupos
	// args[2] = taxa de erro
	// args[3] = tipo: binario, tf ou tfidf
	// args[4] = linhas
	// args[5] = colunas	
	public static void main(String[] args) {
		boolean bomResultado = false; // controla o fato de algoritmo já ter alcançado um bom resultado ou não
		int iteracoes = 0; 
		double jcmAtual = 0;
		int[][] prototiposAnteriorInt;
		double[][] prototiposAnteriorDouble;
		double condicaoParada = 0;
		
		//TESTE
		int[][] resp = null;

		// inicialização do objeto kmeans
		if(args[3].equals("binario") || args[3].equals("tf")){
			KMeansInt kMeans = new KMeansInt(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2]),
					Integer.parseInt(args[4]), Integer.parseInt(args[5]));
			prototiposAnteriorInt = kMeans.definirPrototipos(); // defini prototipos randomicamente
			
			// iteracoes comecam aqui 
			while (!bomResultado) {
				
				iteracoes++;
				kMeans.definirDistanciasEuclidianas();
				kMeans.clustering(); // atualiza matriz de particoes
				jcmAtual = kMeans.calcularJCM(); // calcular o jcm - o qual deseja-se minimizar para otimização
				kMeans.redefinirPrototipos(); // atualizar prototipos
				
				
				
				//testar condição de parada
				condicaoParada = kMeans.diferencaPrototipos(prototiposAnteriorInt);
				if(condicaoParada<kMeans.getTaxaErro()){
					bomResultado = true;
				}
				prototiposAnteriorInt = kMeans.getPrototipos();
				
				//TESTE
				System.out.println("CARREGADO TUN TUN TUN :D");
			}
			resp = kMeans.getMatrizParticao();

		}
		else{
			KMeansDouble kMeans = new KMeansDouble(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2]),
					Integer.parseInt(args[4]), Integer.parseInt(args[5]));
	
		
			prototiposAnteriorDouble = kMeans.definirPrototipos(); // defini prototipos randomicamente
							
			// iteracoes comecam aqui 
			while (!bomResultado || iteracoes < 10) {
				
				iteracoes++;
				kMeans.definirDistanciasEuclidianas();
				kMeans.clustering(); // atualiza matriz de particoes
				jcmAtual = kMeans.calcularJCM(); // calcular o jcm - o qual deseja-se minimizar para otimização
				kMeans.redefinirPrototipos(); // atualizar prototipos
				
				
				
				//testar condição de parada
				condicaoParada = kMeans.diferencaPrototipos(prototiposAnteriorDouble);
				if(condicaoParada<kMeans.getTaxaErro()){
					bomResultado = true;
				}
				prototiposAnteriorDouble = kMeans.getPrototipos(); 
				
				//TESTE
				System.out.println("CARREGADO TUN TUN TUN :D");
				resp = kMeans.getMatrizParticao();
			}
		}

		
		
		// escrever em um arquivo a matriz de particao final para analise dos grupos formados
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter("resp.csv"));
			for(int i = 0; i < Integer.parseInt(args[1]); i++){
				for(int j = 0; j < Integer.parseInt(args[4]); j++){
					br.append(resp[i][j] + ",");
				}
				br.newLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

}
