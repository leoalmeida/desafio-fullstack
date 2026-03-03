package com.example.backend.config;

import com.example.ejb.BeneficioEjbService;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EjbConfig {

    /**
     * Configura o BeneficioEjbService para ser gerenciado pelo Spring, permitindo a
     * injeção de dependências.
     * O EntityManager é injetado manualmente usando reflection, já que o EJB não é
     * um componente Spring tradicional.
     *
     * @param em EntityManager gerenciado pelo Spring para acesso ao banco de dados
     * @return Instância configurada do BeneficioEjbService com EntityManager
     *         injetado
     */
    @Bean
    public BeneficioEjbService beneficioEjbService(@Nonnull final EntityManager em) {
        BeneficioEjbService ejb = new BeneficioEjbService(em);
        return ejb;
    }

    /* Configuração para utilizar o EJB remoto via JNDI.
     * Requer que o servidor de aplicação esteja configurado para expor o EJB remotamente.
     * O método context() cria um InitialContext com as propriedades necessárias para se conectar ao servidor.
     * O método beneficioEjbServiceRemote() realiza a lookup do EJB remoto usando o nome JNDI configurado no servidor.
     * Ajuste os parâmetros de conexão e o nome JNDI conforme a configuração do seu ambiente de desenvolvimento.
     */
    /*
    @Bean
    public Context context() throws NamingException {
        Hashtable<String, String> jndiProps = new Hashtable<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8080"); // Adjust to your server
        jndiProps.put("jboss.naming.client.ejb.context", "true");
        Context ctx = new InitialContext(jndiProps);

        // JNDI name depends on your server configuration
        return ctx;
    }
    @Bean
    public BeneficioEjbServiceRemote beneficioEjbServiceRemote(Context context) throws NamingException {
        return (BeneficioEjbServiceRemote)
            context.lookup("ejb-module/BeneficioEjbServiceRemote!com.example.ejb.BeneficioEjbServiceRemote");
    }
    */
}
