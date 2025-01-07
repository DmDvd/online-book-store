package com.example.library.repository;

import com.example.library.exception.EntityNotFoundException;
import com.example.library.model.Book;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Override
    public Book createBook(Book book) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save book " + book, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Book getBookById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            if (book == null) {
                throw new EntityNotFoundException("Book with id " + id + " not found");
            }
            return book;
        } catch (Exception e) {
            throw new RuntimeException("Can't get book by id " + id, e);
        }
    }

    @Override
    public List<Book> getAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<Book> criteriaQuery = session.getCriteriaBuilder()
                    .createQuery(Book.class);
            criteriaQuery.from(Book.class);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't find all books", e);
        }
    }
}
