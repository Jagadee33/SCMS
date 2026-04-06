import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { libraryAPI, bookIssueAPI, bookReservationAPI, libraryStatsAPI } from '../services/libraryAPI';

const LibraryDashboard = () => {
  const { user } = useAuth();
  const [viewMode, setViewMode] = useState('catalog');
  const [books, setBooks] = useState([]);
  const [currentIssues, setCurrentIssues] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [libraryStats, setLibraryStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchFilters, setSearchFilters] = useState({
    title: '',
    author: '',
    category: '',
    language: '',
    publisher: ''
  });
  const [selectedBook, setSelectedBook] = useState(null);
  const [showBookDetails, setShowBookDetails] = useState(false);

  // Fetch library data
  const fetchLibraryData = useCallback(async () => {
    try {
      setLoading(true);
      
      if (user?.id) {
        // Fetch student-specific data
        const [dashboardRes, currentIssuesRes, reservationsRes] = await Promise.all([
          libraryStatsAPI.getStudentLibraryDashboard(user.id),
          bookIssueAPI.getStudentCurrentIssues(user.id),
          bookReservationAPI.getStudentReservations(user.id)
        ]);

        setCurrentIssues(dashboardRes.data.currentIssues || []);
        setReservations(dashboardRes.data.reservations || []);
      }

      // Fetch books catalog
      const booksRes = await libraryAPI.getAllBooks();
      setBooks(booksRes.data || []);

    } catch (error) {
      console.error('Error fetching library data:', error);
    } finally {
      setLoading(false);
    }
  }, [user?.id]);

  useEffect(() => {
    fetchLibraryData();
  }, [fetchLibraryData]);

  // Search books
  const handleSearch = async (query) => {
    try {
      setLoading(true);
      const response = await libraryAPI.searchBooks(query);
      setBooks(response.data || []);
    } catch (error) {
      console.error('Error searching books:', error);
    } finally {
      setLoading(false);
    }
  };

  // Advanced search
  const handleAdvancedSearch = async () => {
    try {
      setLoading(true);
      const response = await libraryAPI.advancedSearch(searchFilters);
      setBooks(response.data || []);
    } catch (error) {
      console.error('Error in advanced search:', error);
    } finally {
      setLoading(false);
    }
  };

  // Reserve book
  const handleReserveBook = async (bookId) => {
    if (!user?.id) {
      alert('Please login to reserve books');
      return;
    }

    try {
      await bookReservationAPI.reserveBook(bookId, user.id);
      alert('Book reserved successfully!');
      fetchLibraryData(); // Refresh data
    } catch (error) {
      console.error('Error reserving book:', error);
      alert(error.response?.data?.error || 'Failed to reserve book');
    }
  };

  // Issue book (for librarians)
  const handleIssueBook = async (bookId) => {
    if (!user?.id) {
      alert('Please login to issue books');
      return;
    }

    try {
      await bookIssueAPI.issueBook(bookId, user.id, user.id);
      alert('Book issued successfully!');
      fetchLibraryData(); // Refresh data
    } catch (error) {
      console.error('Error issuing book:', error);
      alert(error.response?.data?.error || 'Failed to issue book');
    }
  };

  // Return book
  const handleReturnBook = async (issueId) => {
    try {
      await bookIssueAPI.returnBook(issueId, user.id);
      alert('Book returned successfully!');
      fetchLibraryData(); // Refresh data
    } catch (error) {
      console.error('Error returning book:', error);
      alert(error.response?.data?.error || 'Failed to return book');
    }
  };

  // Renew book
  const handleRenewBook = async (issueId) => {
    try {
      await bookIssueAPI.renewBook(issueId, user.id);
      alert('Book renewed successfully!');
      fetchLibraryData(); // Refresh data
    } catch (error) {
      console.error('Error renewing book:', error);
      alert(error.response?.data?.error || 'Failed to renew book');
    }
  };

  // Cancel reservation
  const handleCancelReservation = async (reservationId) => {
    try {
      await bookReservationAPI.cancelReservation(reservationId, user.id);
      alert('Reservation cancelled successfully!');
      fetchLibraryData(); // Refresh data
    } catch (error) {
      console.error('Error cancelling reservation:', error);
      alert(error.response?.data?.error || 'Failed to cancel reservation');
    }
  };

  // Format date
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  // Get book status color
  const getBookStatusColor = (status) => {
    const colors = {
      'AVAILABLE': 'text-green-600 bg-green-50',
      'ISSUED': 'text-blue-600 bg-blue-50',
      'RESERVED': 'text-yellow-600 bg-yellow-50',
      'DAMAGED': 'text-red-600 bg-red-50',
      'LOST': 'text-red-600 bg-red-50'
    };
    return colors[status] || 'text-gray-600 bg-gray-50';
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
          <p className="mt-4 text-gray-600">Loading library...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="container mx-auto px-4 py-4">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 flex items-center">
                <span className="mr-3">📚</span>
                Library Dashboard
              </h1>
              <p className="text-gray-600 text-sm mt-1">
                Welcome to the digital library
              </p>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={() => setViewMode('catalog')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'catalog' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📖 Catalog
              </button>
              <button
                onClick={() => setViewMode('my-books')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'my-books' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📚 My Books
              </button>
              <button
                onClick={() => setViewMode('reservations')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'reservations' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                🔖 Reservations
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {viewMode === 'catalog' && (
          <>
            {/* Search Section */}
            <div className="bg-white rounded-lg shadow p-6 mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Search Books</h2>
              
              {/* Quick Search */}
              <div className="mb-6">
                <div className="flex space-x-4">
                  <input
                    type="text"
                    placeholder="Search by title, author, ISBN, or publisher..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearch(searchQuery)}
                    className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <button
                    onClick={() => handleSearch(searchQuery)}
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    Search
                  </button>
                </div>
              </div>

              {/* Advanced Search */}
              <div className="border-t pt-4">
                <h3 className="font-medium text-gray-900 mb-3">Advanced Search</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                  <input
                    type="text"
                    placeholder="Title"
                    value={searchFilters.title}
                    onChange={(e) => setSearchFilters({...searchFilters, title: e.target.value})}
                    className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <input
                    type="text"
                    placeholder="Author"
                    value={searchFilters.author}
                    onChange={(e) => setSearchFilters({...searchFilters, author: e.target.value})}
                    className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <input
                    type="text"
                    placeholder="Category"
                    value={searchFilters.category}
                    onChange={(e) => setSearchFilters({...searchFilters, category: e.target.value})}
                    className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <input
                    type="text"
                    placeholder="Language"
                    value={searchFilters.language}
                    onChange={(e) => setSearchFilters({...searchFilters, language: e.target.value})}
                    className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <input
                    type="text"
                    placeholder="Publisher"
                    value={searchFilters.publisher}
                    onChange={(e) => setSearchFilters({...searchFilters, publisher: e.target.value})}
                    className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                  <button
                    onClick={handleAdvancedSearch}
                    className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700"
                  >
                    Advanced Search
                  </button>
                </div>
              </div>
            </div>

            {/* Books Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {books.map((book) => (
                <div key={book.id} className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow p-6">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex-1">
                      <h3 className="font-semibold text-gray-900 text-lg">{book.title}</h3>
                      <p className="text-gray-600 text-sm">by {book.author}</p>
                    </div>
                    {book.coverImageUrl && (
                      <img 
                        src={book.coverImageUrl} 
                        alt={book.title}
                        className="w-16 h-20 object-cover rounded"
                      />
                    )}
                  </div>
                  
                  <div className="space-y-2 text-sm text-gray-600">
                    <p><span className="font-medium">ISBN:</span> {book.isbn}</p>
                    <p><span className="font-medium">Category:</span> {book.category}</p>
                    <p><span className="font-medium">Language:</span> {book.language}</p>
                    <p><span className="font-medium">Available:</span> {book.availableCopies}/{book.totalCopies}</p>
                  </div>

                  <div className="mt-4 flex items-center justify-between">
                    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${getBookStatusColor(book.status)}`}>
                      {book.status}
                    </span>
                    <div className="flex space-x-2">
                      {book.status === 'AVAILABLE' && (
                        <button
                          onClick={() => handleReserveBook(book.id)}
                          className="px-3 py-1 bg-yellow-600 text-white text-xs rounded hover:bg-yellow-700"
                        >
                          Reserve
                        </button>
                      )}
                      <button
                        onClick={() => {
                          setSelectedBook(book);
                          setShowBookDetails(true);
                        }}
                        className="px-3 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700"
                      >
                        Details
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}

        {viewMode === 'my-books' && (
          <div className="space-y-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">My Issued Books</h2>
            {currentIssues.length === 0 ? (
              <div className="bg-white rounded-lg shadow p-8 text-center">
                <p className="text-gray-600">You have no books currently issued.</p>
              </div>
            ) : (
              <div className="space-y-4">
                {currentIssues.map((issue) => (
                  <div key={issue.id} className="bg-white rounded-lg shadow p-6">
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-900 text-lg">{issue.book.title}</h3>
                        <p className="text-gray-600">by {issue.book.author}</p>
                        <div className="mt-2 space-y-1 text-sm text-gray-600">
                          <p><span className="font-medium">Issue Date:</span> {formatDate(issue.issueDate)}</p>
                          <p><span className="font-medium">Due Date:</span> {formatDate(issue.dueDate)}</p>
                          <p><span className="font-medium">Renewals:</span> {issue.renewalCount}/{issue.maxRenewals}</p>
                        </div>
                      </div>
                      <div className="flex space-x-2">
                        {issue.renewalCount < issue.maxRenewals && (
                          <button
                            onClick={() => handleRenewBook(issue.id)}
                            className="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700"
                          >
                            Renew
                          </button>
                        )}
                        <button
                          onClick={() => handleReturnBook(issue.id)}
                          className="px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700"
                        >
                          Return
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {viewMode === 'reservations' && (
          <div className="space-y-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">My Reservations</h2>
            {reservations.length === 0 ? (
              <div className="bg-white rounded-lg shadow p-8 text-center">
                <p className="text-gray-600">You have no active reservations.</p>
              </div>
            ) : (
              <div className="space-y-4">
                {reservations.map((reservation) => (
                  <div key={reservation.id} className="bg-white rounded-lg shadow p-6">
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-900 text-lg">{reservation.book.title}</h3>
                        <p className="text-gray-600">by {reservation.book.author}</p>
                        <div className="mt-2 space-y-1 text-sm text-gray-600">
                          <p><span className="font-medium">Reservation Date:</span> {formatDate(reservation.reservationDate)}</p>
                          <p><span className="font-medium">Expires:</span> {formatDate(reservation.expiryDate)}</p>
                          {reservation.notes && <p><span className="font-medium">Notes:</span> {reservation.notes}</p>}
                        </div>
                      </div>
                      <div className="flex space-x-2">
                        <button
                          onClick={() => handleCancelReservation(reservation.id)}
                          className="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700"
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Book Details Modal */}
        {showBookDetails && selectedBook && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-screen overflow-y-auto">
              <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                  <h2 className="text-xl font-bold text-gray-900">{selectedBook.title}</h2>
                  <button
                    onClick={() => setShowBookDetails(false)}
                    className="text-gray-400 hover:text-gray-600"
                  >
                    ✕
                  </button>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <div>
                      <h3 className="font-semibold text-gray-900 mb-2">Book Information</h3>
                      <div className="space-y-2 text-sm">
                        <p><span className="font-medium">Author:</span> {selectedBook.author}</p>
                        <p><span className="font-medium">ISBN:</span> {selectedBook.isbn}</p>
                        <p><span className="font-medium">Publisher:</span> {selectedBook.publisher}</p>
                        <p><span className="font-medium">Edition:</span> {selectedBook.edition || 'N/A'}</p>
                        <p><span className="font-medium">Publication Year:</span> {selectedBook.publicationYear || 'N/A'}</p>
                        <p><span className="font-medium">Pages:</span> {selectedBook.pages || 'N/A'}</p>
                        <p><span className="font-medium">Language:</span> {selectedBook.language}</p>
                      </div>
                    </div>
                    
                    <div className="space-y-4">
                      <div>
                        <h3 className="font-semibold text-gray-900 mb-2">Availability</h3>
                        <div className="space-y-2 text-sm">
                          <p><span className="font-medium">Total Copies:</span> {selectedBook.totalCopies}</p>
                          <p><span className="font-medium">Available Copies:</span> {selectedBook.availableCopies}</p>
                          <p><span className="font-medium">Status:</span> 
                            <span className={`ml-2 px-2 py-1 rounded-full text-xs font-semibold ${getBookStatusColor(selectedBook.status)}`}>
                              {selectedBook.status}
                            </span>
                          </p>
                          {selectedBook.isDigital && (
                            <p><span className="font-medium">Digital:</span> Yes</p>
                          )}
                        </div>
                      </div>
                      
                      {selectedBook.description && (
                        <div>
                          <h3 className="font-semibold text-gray-900 mb-2">Description</h3>
                          <p className="text-sm text-gray-600">{selectedBook.description}</p>
                        </div>
                      )}
                    </div>
                  </div>
                  
                  <div className="mt-6 flex justify-end space-x-3">
                    {selectedBook.status === 'AVAILABLE' && (
                      <button
                        onClick={() => {
                          handleReserveBook(selectedBook.id);
                          setShowBookDetails(false);
                        }}
                        className="px-4 py-2 bg-yellow-600 text-white rounded hover:bg-yellow-700"
                      >
                        Reserve Book
                      </button>
                    )}
                    <button
                      onClick={() => setShowBookDetails(false)}
                      className="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300"
                    >
                      Close
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default LibraryDashboard;
