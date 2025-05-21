using Microsoft.EntityFrameworkCore;

namespace SmakApi.Data.Repositories;

public class Repository<T> : IRepository<T> where T : class
{
    protected readonly SmakDbContext _context;

    public Repository(SmakDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<T>> GetAllAsync() => await _context.Set<T>().ToListAsync();

    public async Task<T?> GetByIdAsync(Guid id) => await _context.Set<T>().FindAsync(id);

    public async Task AddAsync(T entity) => await _context.Set<T>().AddAsync(entity);

    public void Update(T entity) => _context.Set<T>().Update(entity);

    public void Delete(T entity) => _context.Set<T>().Remove(entity);

    public async Task SaveChangesAsync() => await _context.SaveChangesAsync();
}

