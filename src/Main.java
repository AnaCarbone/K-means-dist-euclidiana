
public class Main {
	// args[0] = nome do arquivo
	// args[1] = k grupos
	// args[2] = taxa de erro
	public static void main(String [] args)
	{
		boolean bomResultado = false;
		int iteracoes = 0;
		double jcmAtual = 0;
		
		// ver como vai passar essas coisas nome, k e erro
		KMeans kMeans = new KMeans(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2]));
		kMeans.definirPrototipos(); // defini prototipos randomicamente
		
		// iteracoes comecam aqui
		while(!bomResultado)
		{
			iteracoes++;
			kMeans.definirDistanciasEuclidianas();
			kMeans.clustering(); // atualiza matriz de particoes
			jcmAtual = kMeans.calcularJCM();
			if(iteracoes == 1)
			{
				kMeans.setJCM(jcmAtual);
			}
			else
			{
				double diferencaJCM = kMeans.getJCM() - jcmAtual;
				if(diferencaJCM < kMeans.getTaxaErro())
				{
					//NAO PRECISA DOS DOIS
					bomResultado = true;
					break;
				}
				else
				{
					kMeans.setJCM(jcmAtual); // atualiza o jcm para o último
					kMeans.redefinirPrototipos(); // atualizar prototipos
				}
			}
		}
		
	}
}
