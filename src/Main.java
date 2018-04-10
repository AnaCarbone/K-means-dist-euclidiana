import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	// args[0] = nome do arquivo
	// args[1] = k grupos
	// args[2] = taxa de erro
	// args[3] = linhas
	// args[4] = colunas
	public static void main(String[] args) {
		boolean bomResultado = false; // controla o fato de algoritmo já ter alcançado um bom resultado ou não
		int iteracoes = 0; 
		double jcmAtual = 0;
		double [][] prototiposAnterior;	
		double condicaoParada = 0;

		// inicialização do objeto kmeans
		KMeansDouble kMeans = new KMeansDouble(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2]),
				Integer.parseInt(args[3]), Integer.parseInt(args[4]));
		prototiposAnterior = kMeans.definirPrototipos(); // defini prototipos randomicamente


		// iteracoes comecam aqui 
		while (!bomResultado || iteracoes<100) {
			iteracoes++;
			kMeans.definirDistanciasEuclidianas();
			kMeans.clustering(); // atualiza matriz de particoes
			jcmAtual = kMeans.calcularJCM(); // calcular o jcm - o qual deseja-se minimizar para otimização
			kMeans.redefinirPrototipos(); // atualizar prototipos
			
			//testar condição de parada
			condicaoParada = kMeans.diferencaPrototipos(prototiposAnterior);
			if(condicaoParada<kMeans.getTaxaErro()){
				bomResultado = true;
			}
			System.out.println("CARREGADO TUN TUN TUN :D");
		}
		
		
		// escrever em um arquivo a matriz de particao final para analise dos grupos formados
		int[][] resp = kMeans.getMatrizParticao();
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter("resp.csv"));
			for(int i = 0; i < Integer.parseInt(args[1]); i++){
				for(int j = 0; j < Integer.parseInt(args[3]); j++){
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
