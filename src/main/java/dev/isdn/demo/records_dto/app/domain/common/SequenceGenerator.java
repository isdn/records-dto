package dev.isdn.demo.records_dto.app.domain.common;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Properties;

public class SequenceGenerator implements IdentifierGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public SequenceGenerator() {}

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o)
            throws HibernateException {
        return this.getNumber();
    }

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
    }

    public synchronized long getNumber() {
        byte[] bytesPool = new byte[16];
        short i = 0;
        long result = 0L;

        secureRandom.nextBytes(bytesPool);
        for (byte b : bytesPool) {
            if (b != (byte)0) {
                result += ((long) b & 0xFFL) << (8 * i++);
            }
            if (i == 8) break;
        }
        return (result >>> 1);
    }

}
