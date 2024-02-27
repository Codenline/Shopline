// Classe de controller para gerencia o banco de dados apartir das entitys
package Controllers;

import Models.Comandas;
import Models.Produtos;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * @author Andre.infra
 */
public class ComandaController {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("shopLinePU");
    private final EntityManager em = emf.createEntityManager();

    // Procura por um produto com um ID especifico
    public  Comandas findOne(int id) {
        em.getTransaction().begin();
        Comandas entity = em.find(Comandas.class, 1);
        em.getTransaction().commit();

        Integer IdComanda = entity.getIdComanda();
        String NomeCliente = entity.getNomeCliente();
        String Status = entity.getStatus();


        if (entity != null) {

            Comandas Comanda = new Comandas();
            Comanda.setIdComanda(IdComanda);
            Comanda.setNomeCliente(NomeCliente);
            Comanda.setStatus(Status);

            em.close();
            return Comanda;

        } else {
            System.out.println("Produto não encontrado.");
        }
        em.close();
        return null;

    }

    public int ultimoCódigo(){
        int id;
        em.getTransaction().begin();
        TypedQuery<Integer> query = em.createQuery("SELECT MAX(e.codigo) FROM comandas e", Integer.class);
        id = query.getSingleResult();
        if(em.getTransaction() != null && em.getTransaction().isActive()){
        em.getTransaction().rollback();
        }
        else{
        em.close();
        emf.close();
        }


        
        return id;
    }
    
    public Comandas findByNomeCliente(int NomeCliente){
       String jpql = "SELECT p FROM comandas p WHERE p.nome_cliente = :nome_cliente";
        Comandas ComandaFinal;
    em.getTransaction().begin();  
    var query = em.createQuery(jpql, Comandas.class);
    query.setParameter("nome_cliente", NomeCliente);

    List<Comandas> resultados = query.getResultList();

    em.getTransaction().commit();
    ComandaFinal = resultados.get(0);
   
    return ComandaFinal;
    }
    
    public void diminiorEtoque(List<Produtos> listaDeProdutos) {
        for (Produtos produto : listaDeProdutos) {
            int quantidadeDispo = produto.getQuantidadeDisponivel();
            int quandidadeCarrihno = produto.getQuantidade();

            produto.setQuantidadeDisponivel(quantidadeDispo - quandidadeCarrihno);
            System.out.println("Produto" + produto.getNome() + "teve sua quantidade alterada para" + produto.getQuantidadeDisponivel());
            em.getTransaction().begin();
            Produtos produtoChanged = em.find(Produtos.class, produto.getId());
            if (produtoChanged != null) {

                produtoChanged.setCodigo(produto.getCodigo());
                produtoChanged.setNome(produto.getNome());
                produtoChanged.setUnidade(produto.getUnidade());
                produtoChanged.setPreco(produto.getPreco());
                produtoChanged.setQuantidadeDisponivel(produto.getQuantidadeDisponivel());
                produtoChanged.setDataUltimaVenda(produto.getDataUltimaVenda());
            }
            em.merge(produtoChanged);
            em.getTransaction().commit();
           
            
        }
         em.close();
    }

    // Lista todos os produtos da base
    public List<Comandas> findMany() {
       
        em.getTransaction().begin();

        String jpql = "SELECT p FROM Produtos p"; // Consulta JPQL para selecionar todos os registros
        TypedQuery<Comandas> query = em.createQuery(jpql, Comandas.class);

        List<Comandas> Comandas = query.getResultList(); // Obtém todos os registros da tabela

        em.getTransaction().commit();
        em.close();
        emf.close();
        return Comandas;
    }

    // Cria uma nova entry de produto.
    public void createOne(String NomeCliente) {

        Comandas NovaComanda = new Comandas();
        NovaComanda.setNomeCliente(NomeCliente);
        NovaComanda.setStatus("aberto");
        em.getTransaction().begin();
        em.persist(NovaComanda);
        em.getTransaction().commit();
        em.close();
    }
    // exclui um produto pelo ID.

    public void deleteOne(int id) {
        em.getTransaction().begin();

        Comandas ComadnaDeleted = em.find(Comandas.class, id);
        if (ComadnaDeleted != null) {
            em.remove(ComadnaDeleted);
            em.getTransaction().commit();
            em.close();
        }

    }

    public void changeOne(int id, Produtos ProdutoData) {
        em.getTransaction().begin();
        Produtos produtoChanged = em.find(Produtos.class, id);

        if (produtoChanged != null) {

            produtoChanged.setCodigo(ProdutoData.getCodigo());
            produtoChanged.setNome(ProdutoData.getNome());
            produtoChanged.setUnidade(ProdutoData.getUnidade());
            produtoChanged.setPreco(ProdutoData.getPreco());
            produtoChanged.setQuantidadeDisponivel(ProdutoData.getQuantidadeDisponivel());
            produtoChanged.setDataUltimaVenda(ProdutoData.getDataUltimaVenda());
        }

        em.merge(produtoChanged);
        em.getTransaction().commit();
        em.close();
    }
}