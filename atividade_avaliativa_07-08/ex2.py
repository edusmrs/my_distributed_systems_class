from concurrent.futures import ThreadPoolExecutor
from util import limpar_dados

def exercicio_2():
    nomes_sujos = [f"   nome de usuario_{i}   \n" for i in range(5000)]
    
    meio = len(nomes_sujos) // 2
    bloco_a = nomes_sujos[:meio]
    bloco_b = nomes_sujos[meio:]
    
    with ThreadPoolExecutor(max_workers=2) as executor:
        futuro_a = executor.submit(limpar_dados, bloco_a)
        futuro_b = executor.submit(limpar_dados, bloco_b)

        # sem compartilhar memoria entre threads
        lista_limpa_a = futuro_a.result()
        lista_limpa_b = futuro_b.result()
        
    lista_final = lista_limpa_a + lista_limpa_b
    
    print(f"total de registros processados: {len(lista_final)}")
    print(f"amostra original: '{nomes_sujos[0]}'")
    print(f"amostra processada thread A: '{lista_final[0]}'")
    print(f"amostra processada thread B: '{lista_final[-1]}'")

if __name__ == "__main__":
    exercicio_2()