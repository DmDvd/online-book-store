package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.library.dto.book.BookDto;
import com.example.library.dto.book.BookSearchParametersDto;
import com.example.library.dto.book.CreateBookRequestDto;
import com.example.library.exception.EntityNotFoundException;
import com.example.library.mapper.BookMapper;
import com.example.library.model.Book;
import com.example.library.repository.book.BookRepository;
import com.example.library.repository.book.BookSpecificationBuilder;
import com.example.library.service.book.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Get book by valid ID should return correct book")
    void getBookById_WithValidBookId_ShouldReturnValidBook() {
        Long bookId = 1L;
        Book book = new Book()
                .setId(bookId)
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        BookDto bookDto = new BookDto()
                .setId(bookId)
                .setTitle("Sample Book 1")
                .setAuthor("Author B");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.getBookById(bookId);

        assertNotNull(actual);
        assertEquals(bookDto.getTitle(), actual.getTitle());
        assertEquals(bookDto.getAuthor(), actual.getAuthor());
    }

    @Test
    @DisplayName("Get book by non-existing ID should throw exception")
    void getBookById_WithNonExistingUserId_ShouldThrowException() {
        Long bookId = 100L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                RuntimeException.class, () -> bookService.getBookById(bookId)
        );
        String expected = "Can't find book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Create a book with valid request should return BookDto")
    void createBook_ValidCreateBookRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        Book book = new Book()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage());

        BookDto bookDto = new BookDto()
                .setId(1L)
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.createBook(requestDto);

        assertThat(savedBookDto).isEqualTo(bookDto);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get all books with valid pageable should return book list")
    void getAll_ValidPageable_ReturnsAllBook() {
        Book book = new Book()
                .setId(1L)
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        BookDto bookDto = new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> bookDtos = bookService.getAll(pageable);

        assertThat(bookDtos).hasSize(1);
        assertThat(bookDtos.getContent()).containsExactly(bookDto);

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    @DisplayName("Delete book by valid ID should delete book")
    void deleteById_ValidDeleteBook_DeleteBook() {
        Long bookId = 1L;

        doNothing().when(bookRepository).deleteById(bookId);

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update book with valid data should update book")
    void updateBook_ValidUpdateBook_UpdateBook() {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        Book book = new Book()
                .setTitle(requestDto.getTitle())
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage());

        BookDto bookDto = new BookDto()
                .setId(bookId)
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        doNothing().when(bookMapper).updateBookFromDto(requestDto, book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto updateBook = bookService.updateBook(bookId, requestDto);

        assertThat(updateBook).isEqualTo(bookDto);
    }

    @Test
    @DisplayName("Update book with non-existing ID should throw EntityNotFoundException")
    void updateBook_WithNonExistingBook_ShouldThrowEntityNotFoundException() {
        Long bookId = 100L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(bookId, requestDto));
    }

    @Test
    @DisplayName("Search books with valid parameters should return matching books")
    void search_ValidSearchParameters_ReturnsBookDtos() {
        BookSearchParametersDto params = new BookSearchParametersDto(
                new String[]{"Sample Book 1"},
                new String[]{"Author B"},
                new String[]{"0-306-40615-2"}
        );

        Book book = new Book()
                .setId(1L)
                .setTitle("Sample Book 1")
                .setAuthor("Author B")
                .setIsbn("0-306-40615-2")
                .setPrice(BigDecimal.valueOf(149.99))
                .setDescription("Another sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

        BookDto bookDto = new BookDto()
                .setId(book.getId())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn())
                .setPrice(book.getPrice())
                .setDescription(book.getDescription())
                .setCoverImage(book.getCoverImage());

        Specification<Book> specification = mock(Specification.class);
        when(bookSpecificationBuilder.build(params)).thenReturn(specification);

        List<Book> books = List.of(book);
        when(bookRepository.findAll(specification)).thenReturn(books);

        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> result = bookService.search(params);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(bookDto);

        verify(bookRepository, times(1)).findAll(specification);
        verify(bookMapper, times(1)).toDto(book);
        verify(bookSpecificationBuilder, times(1))
                .build(params);
    }
}
