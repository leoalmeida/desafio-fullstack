package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.logging.Logger;

import com.example.ejb.entity.Beneficio;

/* Serviço EJB para operações de negócio relacionadas a Benefícios. */
@Stateless
public class BeneficioEjbService {

    private static final Logger logger = Logger.getLogger(BeneficioEjbService.class.getName());

    @PersistenceContext
    private EntityManager em;

    public BeneficioEjbService() {
        // Construtor padrão necessário para EJB
    }
    public BeneficioEjbService(EntityManager entityManager) {
        this.em = entityManager;
    }
    /**
     * Método deve transferir um valor de um benefício para outro, 
     *  garantindo que o saldo do benefício de origem não fique negativo. 
    */ 
    public void transfer( Long fromId, Long toId, BigDecimal amount) throws IllegalArgumentException, BusinessException {
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("IDs de origem e destino são obrigatórios");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Valor de transferência deve ser positivo");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Não é possível realizar transferência para o mesmo benefício");
        }

        logger.info(String.format("Transferindo: %d -> %d, valor: %s", fromId, toId, amount));
        /*
         * Consulta dos Benefícios:
         *  Solicitação utilizando "pessimistic write lock" nas entidades envolvidas evitando race condition. 
         *  O lock é solicitado em ordem para evitar deadlocks e garantir que as transferências 
         *  não se intercalem, causando perdas de dados ou permitindo saldo ficar negativo. 
         */
        Beneficio from;
        Beneficio to;
        if (fromId < toId) {
            from = em.find(Beneficio.class, fromId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
            to   = em.find(Beneficio.class, toId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        } else {
            to   = em.find(Beneficio.class, toId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
            from = em.find(Beneficio.class, fromId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
        }

        /* Validação dos benefícios encontrados */
        if (from == null || to == null) {
            throw new IllegalArgumentException("Benefício não encontrado");
        }
        if (!from.getAtivo()) {
            throw new BusinessException("Benefício de origem está cancelado");
        }
        if (!to.getAtivo()) {
            throw new BusinessException("Benefício de destino está cancelado");
        }
        if (from.getValor().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente para transferência");
        }

        /* Validação de saldo insuficiente para transferência entre benefícios*/
        BigDecimal newFrom = from.getValor().subtract(amount);
        if (newFrom.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Saldo insuficiente para transferência");
        }

        from.setValor(newFrom);
        to.setValor(to.getValor().add(amount));

        em.merge(from);
        em.merge(to);
        em.flush();
    
    }
}
