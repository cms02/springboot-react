import React, { useEffect, useState } from 'react';
import BookItem from '../../components/BookItem';
import axios from 'axios';

const Home = () => {
  const [books, setBooks] = useState([]);

  //함수 실행시 최초 한번 실행되는 것

  useEffect(() => {
    axios
      .get('/book')
      .then((res) => res.data)
      .then((res) => {
        console.log(1, res);
        setBooks(res);
      }); //비동기 함수
  }, []);

  return (
    <div>
      {books.map((book) => (
        <BookItem key={book.id} book={book} />
      ))}
    </div>
  );
};

export default Home;
